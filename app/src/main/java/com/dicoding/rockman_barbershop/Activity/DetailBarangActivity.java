package com.dicoding.rockman_barbershop.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
//import com.dicoding.rockman_barbershop.Database.Database;
import com.dicoding.rockman_barbershop.Activity.SplashScreen.splash;
import com.dicoding.rockman_barbershop.MainActivity;
import com.dicoding.rockman_barbershop.R;
import com.dicoding.rockman_barbershop.report.Common;
import com.dicoding.rockman_barbershop.report.PdfDocumentAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class DetailBarangActivity extends AppCompatActivity {
    public final static String ExtraId = "extra_id";

    private ElegantNumberButton kuantitas;

    private TextView nama_produk, stock_produk, harga_produk, keterangan_produk, deskripsi;
    private ImageView foto_produk;

    private ImageView foto_detail;
    private Uri foto_detail_uri = null;
    private Bitmap compressedImageFile;
    private  Button mEditBtn, btnOrder;
    private ImageButton download;

    private String currentDate, idDetail, stringKuantitas;
    private String finish = "Finished", finishing = "Finishing up...", check;
    private Boolean succes = false;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;

    private String string_nama_produk, string_stock_produk, uploadByDetail, string_kode_produk, userId,
            string_harga_produk, string_keterangan_produk, string_foto_produk;

    private int int_harga_produk, int_stock_produk;
    String formattedDate;
    private EditText jumlahOrder;
    private Date dateUpload;
    private String TAG;
    private ProgressDialog loading;
    MediaPlayer mediaPlayer;
    int kuantitasInt;
    PdfWriter writer;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_entry);

        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        mediaPlayer= MediaPlayer.create(DetailBarangActivity.this, R.raw.keranjang);

        jumlahOrder = findViewById(R.id.jumlah_order);
        nama_produk        = findViewById(R.id.detail_nama_produk);
        stock_produk     = findViewById(R.id.detail_stock_produk);
        harga_produk       = findViewById(R.id.detail_harga_produk);
        keterangan_produk   = findViewById(R.id.detail_keterangan_produk);
        deskripsi   = findViewById(R.id.Deskripsi);
//        kuantitas   = (ElegantNumberButton)findViewById(R.id.button_kuantitas);
        btnOrder   = findViewById(R.id.button_cart);

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kuantitasString = jumlahOrder.getText().toString().trim();

                if (kuantitasString.trim().isEmpty()){
                    Toast.makeText(DetailBarangActivity.this, "Isi jumlah order terlebih dahulu",Toast.LENGTH_SHORT).show();
                } else {
                    int ikuantitasInt = Integer.parseInt(kuantitasString);
                    cekStock(ikuantitasInt);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keterangan_produk.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        foto_produk         = findViewById(R.id.detail_foto_produk);
        ImageButton back    = (ImageButton) findViewById(R.id.button_back_select_detail);
        ImageButton update  = (ImageButton) findViewById(R.id.edit_entry);
        download  = (ImageButton) findViewById(R.id.download_entry);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        update.setVisibility(View.GONE);
        userId = fAuth.getCurrentUser().getUid();
        idDetail =  getIntent().getStringExtra(ExtraId);

        loading = ProgressDialog.show(DetailBarangActivity.this,
                null,
                "Mohon Tunggu",
                true,
                false);

        DocumentReference documentReference = fStore.collection("Produk").document(idDetail);
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if (task.isSuccessful()) {
                            /** Memasukan data ke TextView **/
                            
                            string_foto_produk = document != null ? document.getString("foto_url_barang") : null;
                            string_nama_produk = document.getString("nama_barang");
                            string_kode_produk = document.getString("kode_barang");
                            int_stock_produk = document.getLong("stock_barang").intValue();
                            int_harga_produk = document.getLong("harga_barang").intValue();
                            string_keterangan_produk = document.getString("keterangan_barang");

                            string_stock_produk = Integer.toString(int_stock_produk);
                            string_harga_produk = Integer.toString(int_harga_produk);

                            if(Objects.equals(fAuth.getUid(), uploadByDetail)){
                                update.setVisibility(View.VISIBLE);
                            }

                            nama_produk.setText(string_nama_produk);
                            stock_produk.setText(string_stock_produk);
                            harga_produk.setText(string_harga_produk);
                            keterangan_produk.setText(string_keterangan_produk);

                            Picasso.get()
                                    .load(string_foto_produk)
                                    .into(foto_produk);
                            loading.dismiss();

                        }else {
                            Toast.makeText(DetailBarangActivity.this, "Document Tidak Ada", Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                        }
                }
    });

        //perzinan mendownload pdf
        permission();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), EditProdukDipilih.class);
                intent.putExtra(EditProdukDipilih.ExtraId, idDetail);
                intent.putExtra(EditProdukDipilih.ExtraNama, string_nama_produk);
                intent.putExtra(EditProdukDipilih.ExtraKode, string_stock_produk);
                intent.putExtra("ExtraStock", 0);
                intent.putExtra("ExtraHarga", 0);
                intent.putExtra(EditProdukDipilih.ExtraKeterangan, string_keterangan_produk);
//                intent.putExtra(EditProdukDipilih.ExtraTanggal, tanggalBarang);
                intent.putExtra(EditProdukDipilih.ExtraUrl, string_foto_produk);
                startActivity(intent);
                finish();
            }
        });
    }


    private void cekStock(int intJumlah){
        String cek = stock_produk.getText().toString();
        int cekInt = Integer.parseInt(cek);

        if (cekInt-intJumlah <= 0){
            Toast.makeText(DetailBarangActivity.this, "Stock kosong", Toast.LENGTH_SHORT).show();
        } else {
            order(intJumlah);
        }
    }

    private void order(int intJumlah) {
        int addHarga = Integer.parseInt(string_harga_produk);
        int addKuantitas = intJumlah;
        int addTotal = addHarga*addKuantitas;

            loading = ProgressDialog.show(DetailBarangActivity.this,
                    null,
                    "Mohon Tunggu",
                    true,
                    false);



        DocumentReference doc = FirebaseFirestore.getInstance().collection("Pesanan").document();
        String key = doc.getId();

        DocumentReference collectionReference = FirebaseFirestore.getInstance()
                .collection("Pesanan").document(key);

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("kode_pesanan",key );
        postMap.put("kode_barang", string_kode_produk);
        postMap.put("nama_barang", string_nama_produk);
        postMap.put("foto_barang", string_foto_produk);
        postMap.put("user_id", userId);
        postMap.put("harga_barang", int_harga_produk);
        postMap.put("total_harga", addTotal);
        postMap.put("kuantitas", addKuantitas);
        postMap.put("kode_transaksi", "-");
        postMap.put("status", "menunggu");

        collectionReference.set(postMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mediaPlayer.start();
                        loading.dismiss();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailBarangActivity.this, "Pesanan berhasil di tambah", Toast.LENGTH_SHORT).show();
                loading.dismiss();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

    }

    private void permission(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AsyncMakePdf asyncMakePdf=new AsyncMakePdf();
                                asyncMakePdf.execute();
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }

    public class AsyncMakePdf extends AsyncTask<String,String ,Integer> {

        AlertDialog dialogToShow;
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailBarangActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.preloader_dialog,null);
        TextView txtUpdate = view.findViewById(R.id.txtUpdate);

        @Override
        protected Integer doInBackground(String... strings) {
            createPDFFile(Common.getAppPath(DetailBarangActivity.this)+"Entry.pdf");

            return null;
        }

        private int createPDFFile(String path) {
            if (new File(path).exists())
                new File(path).delete();
            try {
                Document document = new Document();
                //Save
                writer = PdfWriter.getInstance(document, new FileOutputStream(path));

                //Open to write
                document.open();

                //Setting
                document.setPageSize(PageSize.A4);
                document.addCreationDate();
                document.addAuthor("ROCKMAN");
                document.addCreator("ROCKMAN BARBERSHOP");

                //Font Setting
                BaseColor colorAccent = new BaseColor(0, 153, 204, 255);
                BaseColor printAccent = new BaseColor(216, 27, 96);
                BaseColor bluePDBI = new BaseColor(27, 150, 241);//A = 1            float fontSize = 12.0f;
                float valueFontSize = 12.0f;
                float fontSize = 12.0f;

                //Custom Font
                BaseFont fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

                Font regularHead = new Font(fontName, 14, Font.NORMAL, BaseColor.WHITE);
                Font regularReport = new Font(fontName, 30, Font.BOLD, printAccent);
                Font regularTitle = new Font(fontName, 14, Font.NORMAL, BaseColor.BLACK);
                Font regularCategory = new Font(fontName, 12, Font.ITALIC, bluePDBI);
                Font regularSub = new Font(fontName, 12);
                Font regularTotal = new Font(fontName, 16, Font.NORMAL, BaseColor.BLACK);
                Font regularTotalBold = new Font(fontName, 16, Font.NORMAL, BaseColor.BLACK);
                Font footerN = new Font(fontName, 15, Font.NORMAL, printAccent);
                Font footerE = new Font(fontName, 12, Font.NORMAL, BaseColor.DARK_GRAY);


                PdfPTable tableFooter = new PdfPTable(1);
                tableFooter.setTotalWidth(523);

                PdfPCell footerName = new PdfPCell(new Phrase("Rockman Barbershop", footerN));
                PdfPCell footerEmail = new PdfPCell(new Phrase("rockman-barbershop.business.site", footerE));

                PdfPCell footerEmpty = new PdfPCell(new Phrase(""));

                footerName.setBorder(Rectangle.NO_BORDER);
                footerEmpty.setBorder(Rectangle.NO_BORDER);
                footerEmail.setBorder(Rectangle.NO_BORDER);

                PdfPCell preBorderBlue = new PdfPCell(new Phrase(""));
                preBorderBlue.setMinimumHeight(3f);
                preBorderBlue.setUseVariableBorders(true);
                preBorderBlue.setBorder(Rectangle.TOP);
                preBorderBlue.setBorderColorTop(bluePDBI);
                preBorderBlue.setBorderWidthTop(3);
                tableFooter.addCell(preBorderBlue);
                tableFooter.addCell(footerName);
                tableFooter.addCell(footerEmail);

                HeaderFooter event = new HeaderFooter(tableFooter);
                writer.setPageEvent(event);
//            document.open();

                onProgressUpdate("Please wait...");

                //Header

                PdfPTable tableHeader = new PdfPTable(1);
                tableHeader.setWidthPercentage(100);


                try {
                    Drawable d = getResources().getDrawable(R.drawable.kop_rockman);
                    BitmapDrawable bitDw = ((BitmapDrawable) d);
                    Bitmap bmp = bitDw.getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image image = Image.getInstance(stream.toByteArray());
                    // image.scaleToFit(50, 50);
                    PdfPCell preImage = new PdfPCell(image, true);
                    preImage.setBorder(Rectangle.NO_BORDER);
                    preImage.setVerticalAlignment(Element.ALIGN_BOTTOM);

                    tableHeader.addCell(preImage);

                    document.add(tableHeader);


                } catch (BadElementException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                //header

                PdfPTable tableHeading = new PdfPTable(2);
                tableHeading.setWidthPercentage(100);
                tableHeading.setSpacingBefore(50);

                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                formattedDate = df.format(c);


                PdfPCell preAddress = new PdfPCell(new Phrase("", regularCategory));
                PdfPCell preName = new PdfPCell(new Phrase("Admin", regularTitle));
                PdfPCell preDate = new PdfPCell(new Phrase("DATE: " + formattedDate, regularTitle));
                PdfPCell preBill = new PdfPCell(new Phrase("No : 0001", regularTitle));

                preBill.setVerticalAlignment(Element.ALIGN_BOTTOM);
                preBill.setHorizontalAlignment(Element.ALIGN_RIGHT);

                preDate.setVerticalAlignment(Element.ALIGN_BOTTOM);
                preDate.setHorizontalAlignment(Element.ALIGN_RIGHT);
                preName.setBorder(Rectangle.NO_BORDER);
                preAddress.setBorder(Rectangle.NO_BORDER);
                preDate.setBorder(Rectangle.NO_BORDER);
                preBill.setBorder(Rectangle.NO_BORDER);

                try {
                    tableHeading.addCell(preAddress);
                    tableHeading.addCell(preBill);
                    tableHeading.addCell(preName);
                    tableHeading.addCell(preDate);
                    document.add(tableHeading);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                onProgressUpdate(" items processing...");

                //Isi

                PdfPTable tableIsi = new PdfPTable(1);
                tableIsi.setWidthPercentage(80);
                tableIsi.setSpacingBefore(50);
                tableIsi.setSpacingAfter(30);
//                tableIsi.setComplete(false);

                PdfPCell space = new PdfPCell(new Phrase(" ", regularHead));
                PdfPCell preTitle = new PdfPCell(new Phrase(string_nama_produk, regularTitle));
                PdfPCell preCategory = new PdfPCell(new Phrase(string_harga_produk + " - " + string_stock_produk, regularCategory));
                PdfPCell preBody = new PdfPCell(new Phrase(string_keterangan_produk, regularSub));

                preTitle.setVerticalAlignment(Element.ALIGN_BOTTOM);
                preTitle.setHorizontalAlignment(Element.ALIGN_CENTER);

                preCategory.setVerticalAlignment(Element.ALIGN_BOTTOM);
                preCategory.setHorizontalAlignment(Element.ALIGN_CENTER);

                preBody.setVerticalAlignment(Element.ALIGN_BOTTOM);
                preBody.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);

                preTitle.setBorder(Rectangle.NO_BORDER);
                preCategory.setBorder(Rectangle.NO_BORDER);
                preBody.setBorder(Rectangle.NO_BORDER);
                space.setBorder(Rectangle.NO_BORDER);

                try {

                    tableIsi.addCell(preTitle);
                    tableIsi.addCell(preCategory);
                    tableIsi.addCell(space);
                    tableIsi.addCell(preBody);

                    document.add(tableIsi);

                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                //Image entry

                try {

                    PdfPTable tableImageEntry = new PdfPTable(1);
                    tableImageEntry.setWidthPercentage(50);
                    ByteArrayOutputStream stream2 = new ByteArrayOutputStream();

                    Image image = Image.getInstance(string_foto_produk);
                    PdfPCell preImageProfile = new PdfPCell(image, true);

                    preImageProfile.setVerticalAlignment(Element.ALIGN_CENTER);
                    preImageProfile.setHorizontalAlignment(Element.ALIGN_CENTER);

                    preImageProfile.setBorder(Rectangle.NO_BORDER);

                    tableImageEntry.addCell(preImageProfile);

                    document.add(tableImageEntry);

//                    tableIsi.setComplete(true);

                } catch (IOException | DocumentException e) {
                    e.printStackTrace();
                }

                PdfPTable ttd = new PdfPTable(2);
                ttd.setWidthPercentage(80);
                ttd.setSpacingBefore(60);

                PdfPCell tgl = new PdfPCell(new Phrase("Bogor, "+formattedDate, regularTitle));
                PdfPCell admin = new PdfPCell(new Phrase("Pemilik", regularTitle));
                PdfPCell namaAdmin = new PdfPCell(new Phrase("Muhammad Ramlan Saputra", regularTitle));

                tgl.setVerticalAlignment(Element.ALIGN_BOTTOM);
                tgl.setHorizontalAlignment(Element.ALIGN_CENTER);

                namaAdmin.setVerticalAlignment(Element.ALIGN_BOTTOM);
                namaAdmin.setHorizontalAlignment(Element.ALIGN_CENTER);

                admin.setVerticalAlignment(Element.ALIGN_BOTTOM);
                admin.setHorizontalAlignment(Element.ALIGN_CENTER);
                admin.setPaddingBottom(50);
                admin.setBorder(Rectangle.NO_BORDER);
                tgl.setBorder(Rectangle.NO_BORDER);
                space.setBorder(Rectangle.NO_BORDER);
                namaAdmin.setBorder(Rectangle.NO_BORDER);


                try {
                    ttd.addCell(space);
                    ttd.addCell(tgl);
                    ttd.addCell(space);
                    ttd.addCell(admin);
                    ttd.addCell(space);
                    ttd.addCell(namaAdmin);

                    document.add(ttd);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                onProgressUpdate(finishing);

                document.close();

            } catch (DocumentException | IOException e) {
                e.printStackTrace();
            }

            return 1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            builder.setView(view);
            builder.setCancelable(false);
            dialogToShow = builder.create();
            dialogToShow.setCanceledOnTouchOutside(false);
            dialogToShow.show();

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            final String a=values[0].toString();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtUpdate.setText(a);
                }
            });
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            check = txtUpdate.getText().toString();

            if (check.equals(finishing)){
                txtUpdate.setText(finish);
                dialogToShow.dismiss();
                printPDF();
            } else {
                Toast.makeText(DetailBarangActivity.this, "Report gagal", Toast.LENGTH_SHORT).show();
                dialogToShow.dismiss();
            }

        }

        private void printPDF() {
        PrintManager printManager = (PrintManager)getSystemService(Context.PRINT_SERVICE);
        try {
            PdfDocumentAdapter.printName = "Profile_Report_" + formattedDate;
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(DetailBarangActivity.this,
                    Common.getAppPath(DetailBarangActivity.this)+"Entry.pdf");
            Objects.requireNonNull(printManager).print("Document",printDocumentAdapter, new PrintAttributes.Builder().build());
        } catch (Exception ex){
            Log.e("ROCKMAN BARBERSHOP", ""+ex.getMessage());
        }
    }
    }

//    private void addNewItemWithLeftAndRight(Document document, String textLeft, String textRight, Font textLeftFont, Font textRightFont) throws DocumentException {
//        Chunk chunkTextLeft = new Chunk(textLeft,textLeftFont);
//        Chunk chunkTextRight = new Chunk(textLeft,textRightFont);
//        Paragraph p = new Paragraph(chunkTextLeft);
//        p.add(new Chunk(new VerticalPositionMark()));
//        p.add(chunkTextRight);
//        document.add(p);
//    }
//
//    private void addLineSeparator(Document document) throws DocumentException {
//        LineSeparator lineSeparator = new LineSeparator();
//        lineSeparator.setLineColor(new BaseColor(0,0,0,68));
//        addLineSpace(document);
//        document.add(new Chunk(lineSeparator));
//        addLineSpace(document);
//    }
//
//    private void addLineSpace(Document document) throws DocumentException {
//        document.add(new Paragraph(""));
//    }
//
//    private void addNewItem(Document document, String text, int align, Font font) throws DocumentException {
//        Chunk chunk = new Chunk(text,font);
//        Paragraph paragraph = new Paragraph(chunk);
//        paragraph.setAlignment(align);
//        document.add(paragraph);
//    }
}
