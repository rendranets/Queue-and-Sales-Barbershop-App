package com.dicoding.rockman_barbershop.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.dicoding.rockman_barbershop.R;
import com.dicoding.rockman_barbershop.adapter.DetailRiwayatTransaksiAdapter;
import com.dicoding.rockman_barbershop.model.Produk;
import com.dicoding.rockman_barbershop.model.RiwayatPesanan;
import com.dicoding.rockman_barbershop.model.RiwayatTransaksi;
import com.dicoding.rockman_barbershop.report.Common;
import com.dicoding.rockman_barbershop.report.ConstTransaksi;
import com.dicoding.rockman_barbershop.report.ConstTransaksiUser;
import com.dicoding.rockman_barbershop.report.PdfDocumentAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.dicoding.rockman_barbershop.report.ConstAntrian.antrianList;
import static com.dicoding.rockman_barbershop.report.ConstProduk.terbaruList;
import static com.dicoding.rockman_barbershop.report.ConstTransaksi.transaksiList;
import static com.dicoding.rockman_barbershop.report.ConstTransaksiUser.kodeTransaksi;
import static com.dicoding.rockman_barbershop.report.ConstTransaksiUser.transaksiUserList;
import static com.dicoding.rockman_barbershop.report.ConstUser.userList;

public class DetailRiwayatTransaksi extends AppCompatActivity {

    public final static String ExtraId = "extra_id";

    private ElegantNumberButton kuantitas;

    private TextView nama_produk, stock_produk, harga_produk, keterangan_produk, deskripsi;
    private ImageView foto_produk;

    private ImageView foto_detail;
    private Uri foto_detail_uri = null;
    private Bitmap compressedImageFile;
    private Button mEditBtn, btnOrder;
    private ImageButton download, editStatus;

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = mDatabase.collection("Pesanan");

    private String currentDate, idDetail, stringKuantitas;
    private String finish = "Finished", finishing = "Finishing up...", check;
    private Boolean succes = false;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;

    private String string_nama_produk, string_stock_produk, uploadByDetail, string_kode_produk, userId,
            string_harga_produk, string_keterangan_produk, string_foto_produk, hargaIntent
            , tanggalIntent, statusIntent;

    private int int_harga_produk, int_stock_produk, totalHargaTransaksi;
    String formattedDate;

    DetailRiwayatTransaksiAdapter detailRiwayatTransaksiAdapter;
    private RecyclerView recyclerView1;

    private Date dateUpload;
    private String TAG, id, idT,tipeUser;
    private ProgressDialog loading;
    TextView tvTotalHarga;

    PdfWriter writer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_riwayat_transaksi);

        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

//        kuantitas   = (ElegantNumberButton)findViewById(R.id.button_kuantitas);

        id = getIntent().getStringExtra("ExtraRiwayatTransaksiId");
        idT = getIntent().getStringExtra("ExtraRiwayatTransaksiIdTransaksi");
        tipeUser = getIntent().getStringExtra("ExtraRiwayatTransaksiTipe");
        hargaIntent = getIntent().getStringExtra("ExtraRiwayatTransaksiHarga");
        tanggalIntent = getIntent().getStringExtra("ExtraRiwayatTransaksiTanggal");
        statusIntent = getIntent().getStringExtra("ExtraRiwayatTransaksiStatus");
        
//        ImageButton back    = (ImageButton) findViewById(R.id.button_back_transaksi);
        download  = (ImageButton) findViewById(R.id.download_entry);
        tvTotalHarga = findViewById(R.id.total_riwayat_harga_detail);
        recyclerView1 = findViewById(R.id.rv_detail_riwayat_transaksi);
        editStatus = findViewById(R.id.edit_status_riwayat_transaksi);
        editStatus.setVisibility(View.GONE);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userId = fAuth.getCurrentUser().getUid();
        idDetail =  getIntent().getStringExtra(ExtraId);
        permission();
        setUpRecyclerView();
        total();
        detailRiwayatTransaksiAdapter.startListening();

        if (tipeUser.equals("admin")){
            editStatus.setVisibility(View.VISIBLE);
        }

        editStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(DetailRiwayatTransaksi.this, EditStatusTransaksi.class);
                    intent.putExtra("ExtraStatusTransaksiId", id);
                    intent.putExtra("ExtraStatusTransaksiIdTransaksi", idT);
                    startActivity(intent);

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
                                DetailRiwayatTransaksi.AsyncMakePdf asyncMakePdf=new DetailRiwayatTransaksi.AsyncMakePdf();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailRiwayatTransaksi.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.preloader_dialog,null);
        TextView txtUpdate = view.findViewById(R.id.txtUpdate);

        @Override
        protected Integer doInBackground(String... strings) {
            createPDFFile(Common.getAppPath(DetailRiwayatTransaksi.this)+"Entry.pdf");

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

                PdfPCell preReport = new PdfPCell(new Phrase("REPORT", regularReport));

                preReport.setHorizontalAlignment(Element.ALIGN_RIGHT);
                preReport.setVerticalAlignment(Element.ALIGN_TOP);
                preReport.setBorder(Rectangle.NO_BORDER);
                PdfPTable tableHeader = new PdfPTable(2);
                tableHeader.setWidthPercentage(100);

                try {
                    tableHeader.setWidths(new float[]{1, 3});
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                try {
                    Drawable d = getResources().getDrawable(R.drawable.logo_pdbi);
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
                    tableHeader.addCell(preReport);

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

                PdfPCell space = new PdfPCell(new Phrase("", regularHead));
                PdfPCell preTitle = new PdfPCell(new Phrase(idT, regularTitle));
                PdfPCell preCategory = new PdfPCell(new Phrase(statusIntent, regularCategory));
                PdfPCell preBody = new PdfPCell(new Phrase(hargaIntent, regularSub));
                PdfPCell preTgl= new PdfPCell(new Phrase(tanggalIntent, regularSub));

                preTitle.setVerticalAlignment(Element.ALIGN_BOTTOM);
                preTitle.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);

                preCategory.setVerticalAlignment(Element.ALIGN_BOTTOM);
                preCategory.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);

                preBody.setVerticalAlignment(Element.ALIGN_BOTTOM);
                preBody.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);

                preTgl.setVerticalAlignment(Element.ALIGN_BOTTOM);
                preTgl.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);

                preTitle.setBorder(Rectangle.NO_BORDER);
                preCategory.setBorder(Rectangle.NO_BORDER);
                preBody.setBorder(Rectangle.NO_BORDER);
                preTgl.setBorder(Rectangle.NO_BORDER);
                space.setBorder(Rectangle.NO_BORDER);

                try {

                    tableIsi.addCell(preTitle);
                    tableIsi.addCell(preCategory);
                    tableIsi.addCell(preBody);
                    tableIsi.addCell(preTgl);

                    document.add(tableIsi);

                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                //Image entry

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
                Toast.makeText(DetailRiwayatTransaksi.this, "Report gagal", Toast.LENGTH_SHORT).show();
                dialogToShow.dismiss();
            }

        }

        private void printPDF() {
            PrintManager printManager = (PrintManager)getSystemService(Context.PRINT_SERVICE);
            try {
                PdfDocumentAdapter.printName = "Profile_Report_" + formattedDate;
                PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(DetailRiwayatTransaksi.this,
                        Common.getAppPath(DetailRiwayatTransaksi.this)+"Entry.pdf");
                Objects.requireNonNull(printManager).print("Document",printDocumentAdapter, new PrintAttributes.Builder().build());
            } catch (Exception ex){
                Log.e("ROCKMAN BARBERSHOP", ""+ex.getMessage());
            }
        }
    }

    private void setUpRecyclerView() {

        loading = ProgressDialog.show(DetailRiwayatTransaksi.this,
                null,
                "Mohon Tunggu",
                true,
                false);

        Query query = collectionReference
                .whereEqualTo("user_id", userId)
                .whereEqualTo("kode_transaksi", idT);
//                .whereEqualTo("status", "menunggu")
//                    .orderBy("kode_pesanan", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<RiwayatPesanan> options = new FirestoreRecyclerOptions.Builder<RiwayatPesanan>()
                .setQuery(query, RiwayatPesanan.class)
                .build();

        detailRiwayatTransaksiAdapter = new DetailRiwayatTransaksiAdapter(options);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(DetailRiwayatTransaksi.this));
//        recyclerView.padding
        recyclerView1.setAdapter(detailRiwayatTransaksiAdapter);
        loading.dismiss();
    }


    private void total() {
        collectionReference
                .whereEqualTo("user_id", userId)
                .whereEqualTo("kode_transaksi", idT)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    int total = 0;
                    for (QueryDocumentSnapshot document : task.getResult()){
                        int harga = document.getLong("total_harga").intValue();
                        total += harga;
                    }
                    totalHargaTransaksi = total;
                    String totalString = Integer.toString(total);
                    tvTotalHarga.setText(totalString);
                    Log.d("TAG", String.valueOf(total));
                }
            }
        });
    }
}