package com.dicoding.rockman_barbershop.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dicoding.rockman_barbershop.MainActivity;
import com.dicoding.rockman_barbershop.R;
import com.dicoding.rockman_barbershop.model.Antrian;
import com.dicoding.rockman_barbershop.model.RiwayatTransaksi;
import com.dicoding.rockman_barbershop.model.Produk;
import com.dicoding.rockman_barbershop.model.User;
import com.dicoding.rockman_barbershop.report.Common;
import com.dicoding.rockman_barbershop.report.PdfDocumentAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

import static com.dicoding.rockman_barbershop.report.ConstAntrian.antrianList;
import static com.dicoding.rockman_barbershop.report.ConstProduk.terbaruList;
import static com.dicoding.rockman_barbershop.report.ConstTransaksi.transaksiList;
import static com.dicoding.rockman_barbershop.report.ConstUser.userList;

public class AdminPanel extends AppCompatActivity {

    TextView reportUser, reportEntry, categoryEdit, reportAntrian, reportTransaksi;
    Button mEditBtn;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;

    String eName,ePhone;
    private String finish = "Finished", finishing = "Finishing up...", check, stringCount,  stringLencana, lencana;
    private Boolean succes = false;
    private String formattedDate, formattedDateEntry;

    PdfWriter writer;
    private ImageView back;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userId = fAuth.getCurrentUser().getUid();

        reportAntrian = findViewById(R.id.report_antrian);
        reportTransaksi = findViewById(R.id.report_transaksi);
        reportUser = findViewById(R.id.report_user);
        reportEntry = findViewById(R.id.report_entry_terbaru);
        categoryEdit = findViewById(R.id.edit_kategori);
        back = findViewById(R.id.back_admin_panel);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        formattedDate = df.format(c);
        
        permission();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanel.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        categoryEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanel.this, EditCategoryActivity.class);
                startActivity(intent);
            }
        });

    }

    private void permission(){
        Dexter.withActivity(AdminPanel.this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        reportAntrian.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AsyncMakePdfAntrian asyncMakePdf = new AsyncMakePdfAntrian();
                                asyncMakePdf.execute(antrianList(AdminPanel.this));
//                                userList(AdminPanel.this);
                            }


                        });
                        reportTransaksi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AsyncMakePdfTransaksi asyncMakePdf = new AsyncMakePdfTransaksi();
                                asyncMakePdf.execute(transaksiList(AdminPanel.this));
//                                userList(AdminPanel.this);
                            }


                        });
                        reportUser.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AsyncMakePdfUser asyncMakePdf = new AsyncMakePdfUser();
                                asyncMakePdf.execute(userList(AdminPanel.this));
//                                userList(AdminPanel.this);
                            }


                        });

                        reportEntry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AsyncMakePdfTerbaru asyncMakePdfTerbaru = new AsyncMakePdfTerbaru();
                                asyncMakePdfTerbaru.execute(terbaruList(AdminPanel.this));
                                terbaruList(AdminPanel.this);
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


    private class AsyncMakePdfTerbaru extends AsyncTask<ArrayList<Produk>,String, Integer> {

        androidx.appcompat.app.AlertDialog dialogToShow;
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AdminPanel.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.preloader_dialog,null);
        TextView txtUpdate = view.findViewById(R.id.txtUpdate);
        String data = "";

        @SuppressLint("WrongThread")
        @Override
        protected Integer doInBackground(ArrayList<Produk>... arrayLists) {
            try {
                createPDFFile(Common.getAppPath(AdminPanel.this)+"Produk.pdf", arrayLists);
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }

            return null;
        }

        private int createPDFFile(String path, ArrayList<Produk>[] arrayList1) throws IOException, DocumentException{
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
                BaseColor bluePDBI = new BaseColor(27, 150, 170);//A = 1
                //            float fontSize = 12.0f;
                float valueFontSize = 12.0f;
                float fontSize = 12.0f;

                //Custom Font
                BaseFont fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

                Font regularHead = new Font(fontName, 14, Font.NORMAL, BaseColor.BLACK);
                Font regularWhite = new Font(fontName, 14, Font.NORMAL, BaseColor.WHITE);
                Font regularReport = new Font(fontName, 30, Font.BOLD, printAccent);
                Font regularTitle = new Font(fontName, 14, Font.NORMAL, BaseColor.BLACK);
                Font regularCategory = new Font(fontName, 12, Font.ITALIC, bluePDBI);
                Font regularSub = new Font(fontName, 12);
                Font regularTitle16 = new Font(fontName, 16, Font.NORMAL, BaseColor.BLACK);
                Font regularTitle16Blue = new Font(fontName, 16, Font.NORMAL, bluePDBI);
                Font regularTitle16Bold = new Font(fontName, 16, Font.NORMAL, BaseColor.BLACK);
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

                } catch (IOException | DocumentException e) {
                    e.printStackTrace();
                }

                //header

                onProgressUpdate(" items processing...");

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setSpacingBefore(20);
                try {
                    table.setWidths(new float[] {1f,3,2,2,2});
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
//                table.setHeaderRows(1);
//
//                table.setSplitRows(false);
                table.setComplete(false);

                PdfPCell headNo = new PdfPCell(new Phrase("No.",regularWhite));
                PdfPCell headJudul = new PdfPCell(new Phrase("Kode Barang",regularWhite));
                PdfPCell headKategori = new PdfPCell(new Phrase("Nama Barang",regularWhite));
                PdfPCell headProvinsi = new PdfPCell(new Phrase("Harga",regularWhite));
                PdfPCell headDaerah = new PdfPCell(new Phrase("Stock",regularWhite));

                headNo.setPaddingLeft(10);
                headNo.setPaddingTop(5);
                headNo.setPaddingBottom(10);

                headJudul.setPaddingLeft(10);
                headJudul.setPaddingTop(5);
                headJudul.setPaddingBottom(10);

                headKategori.setPaddingLeft(10);
                headKategori.setPaddingTop(5);
                headKategori.setPaddingBottom(10);

                headProvinsi.setPaddingLeft(10);
                headProvinsi.setPaddingTop(5);
                headProvinsi.setPaddingBottom(10);

                headDaerah.setPaddingLeft(10);
                headDaerah.setPaddingTop(5);
                headDaerah.setPaddingBottom(10);

                headNo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headJudul.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headKategori.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headProvinsi.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headDaerah.setVerticalAlignment(Element.ALIGN_MIDDLE);

                headNo.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                headJudul.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                headKategori.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                headProvinsi.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                headDaerah.setHorizontalAlignment(Element.ALIGN_MIDDLE);


                headNo.setBackgroundColor(bluePDBI);
                headJudul.setBackgroundColor(bluePDBI);
                headKategori.setBackgroundColor(bluePDBI);
                headProvinsi.setBackgroundColor(bluePDBI);
                headDaerah.setBackgroundColor(bluePDBI);

                headNo.setBorder(Rectangle.NO_BORDER);
                headJudul.setBorder(Rectangle.NO_BORDER);
                headKategori.setBorder(Rectangle.NO_BORDER);
                headProvinsi.setBorder(Rectangle.NO_BORDER);
                headDaerah.setBorder(Rectangle.NO_BORDER);

//                table.setComplete(true);
                table.addCell(headNo);
                table.addCell(headJudul);
                table.addCell(headKategori);
                table.addCell(headProvinsi);
                table.addCell(headDaerah);


                int n = 0;
                for (int aw=0;aw<arrayList1[0].size();aw++){
                    Produk produk = arrayList1[0].get(aw);

                    onProgressUpdate(String.valueOf(aw)+" items processing...");

                    PdfPCell cellNo = new PdfPCell(new Phrase(String.valueOf(aw+1),regularSub));
                    PdfPCell cellJudul = new PdfPCell(new Phrase(produk.getKode_barang(),regularSub));
                    PdfPCell cellKategori = new PdfPCell(new Phrase(produk.getNama_barang(), regularSub));
                    PdfPCell cellProvinsi = new PdfPCell(new Phrase(String.valueOf(produk.getHarga_barang()),regularSub));

                    PdfPCell cellDaerah = new PdfPCell(new Phrase(String.valueOf(produk.getStock_barang()),regularSub));

                    cellNo.setPaddingLeft(10);
                    cellNo.setPaddingTop(5);
                    cellNo.setPaddingBottom(10);

                    cellJudul.setPaddingLeft(10);
                    cellJudul.setPaddingTop(5);
                    cellJudul.setPaddingBottom(10);

                    cellKategori.setPaddingLeft(10);
                    cellKategori.setPaddingTop(5);
                    cellKategori.setPaddingBottom(10);

                    cellProvinsi.setPaddingLeft(10);
                    cellProvinsi.setPaddingTop(5);
                    cellProvinsi.setPaddingBottom(10);

                    cellDaerah.setPaddingLeft(10);
                    cellDaerah.setPaddingTop(5);
                    cellDaerah.setPaddingBottom(10);

                    cellNo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellJudul.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellKategori.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellProvinsi.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellDaerah.setVerticalAlignment(Element.ALIGN_MIDDLE);

                    cellNo.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cellJudul.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cellKategori.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cellProvinsi.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cellDaerah.setHorizontalAlignment(Element.ALIGN_MIDDLE);

                    cellNo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellJudul.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellKategori.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellProvinsi.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellDaerah.setVerticalAlignment(Element.ALIGN_MIDDLE);

                    cellNo.setBackgroundColor(bluePDBI);
                    cellJudul.setBackgroundColor(bluePDBI);
                    cellKategori.setBackgroundColor(bluePDBI);
                    cellProvinsi.setBackgroundColor(bluePDBI);
                    cellDaerah.setBackgroundColor(bluePDBI);

                    cellNo.setBorder(Rectangle.NO_BORDER);
                    cellJudul.setBorder(Rectangle.NO_BORDER);
                    cellKategori.setBorder(Rectangle.NO_BORDER);
                    cellProvinsi.setBorder(Rectangle.NO_BORDER);
                    cellDaerah.setBorder(Rectangle.NO_BORDER);

                    if (aw%2==0){
                        cellNo.setBackgroundColor(BaseColor.WHITE);
                        cellJudul.setBackgroundColor(BaseColor.WHITE);
                        cellKategori.setBackgroundColor(BaseColor.WHITE);
                        cellProvinsi.setBackgroundColor(BaseColor.WHITE);
                        cellDaerah.setBackgroundColor(BaseColor.WHITE);
                    }else {
                        cellNo.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cellJudul.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cellKategori.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cellProvinsi.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cellDaerah.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    }

                    table.addCell(cellNo);
                    table.addCell(cellJudul);
                    table.addCell(cellKategori);
                    table.addCell(cellProvinsi);
                    table.addCell(cellDaerah);
                }


                table.setComplete(true);

                try {
                    document.add(table);
                } catch (DocumentException ex) {
                    ex.printStackTrace();
                }


                PdfPTable ttd = new PdfPTable(2);
                ttd.setWidthPercentage(80);
                ttd.setSpacingBefore(60);

                PdfPCell space = new PdfPCell(new Phrase("", regularCategory));
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
            AdminPanel.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtUpdate.setText(a);
                }
            });
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

//            txtUpdate.setText(finish);
            check = txtUpdate.getText().toString();

            if (check.equals(finishing)){
                txtUpdate.setText(finish);
                dialogToShow.dismiss();
                Toast.makeText(AdminPanel.this, "Report berhasil dimuat", Toast.LENGTH_SHORT).show();

                printPDF();
            } else {
                Toast.makeText(AdminPanel.this, "Report gagal", Toast.LENGTH_SHORT).show();
                dialogToShow.dismiss();
            }

        }

        private void printPDF() {
            PrintManager printManager = (PrintManager)AdminPanel.this.getSystemService(Context.PRINT_SERVICE);
            try {
                PdfDocumentAdapter.printName = "Produk_Report_" + formattedDate;
                PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(AdminPanel.this,
                        Common.getAppPath(AdminPanel.this)+"Produk.pdf");
                Objects.requireNonNull(printManager).print("Document",printDocumentAdapter, new PrintAttributes.Builder().build());
            } catch (Exception ex){
                Log.e("ROCKMAN BARBERSHOP", ""+ex.getMessage());
            }
        }
    }


    private class AsyncMakePdfUser extends AsyncTask<ArrayList<User>,String, Integer> {

        androidx.appcompat.app.AlertDialog dialogToShow;
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AdminPanel.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.preloader_dialog,null);
        TextView txtUpdate = view.findViewById(R.id.txtUpdate);
        String data = "";

        @SuppressLint("WrongThread")
        @Override
        protected Integer doInBackground(ArrayList<User>... arrayLists) {
            try {
                createPDFFile(Common.getAppPath(AdminPanel.this)+"User.pdf", arrayLists);
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }

            return null;
        }

        private int createPDFFile(String path, ArrayList<User>[] arrayList1) throws IOException, DocumentException{
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
                BaseColor bluePDBI = new BaseColor(27, 150, 170);//A = 1
                //            float fontSize = 12.0f;
                float valueFontSize = 12.0f;
                float fontSize = 12.0f;

                //Custom Font
                BaseFont fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

                Font regularHead = new Font(fontName, 14, Font.NORMAL, BaseColor.BLACK);
                Font regularWhite = new Font(fontName, 14, Font.NORMAL, BaseColor.WHITE);
                Font regularReport = new Font(fontName, 30, Font.BOLD, printAccent);
                Font regularTitle = new Font(fontName, 14, Font.NORMAL, BaseColor.BLACK);
                Font regularCategory = new Font(fontName, 12, Font.ITALIC, bluePDBI);
                Font regularSub = new Font(fontName, 12);
                Font regularTitle16 = new Font(fontName, 16, Font.NORMAL, BaseColor.BLACK);
                Font regularTitle16Blue = new Font(fontName, 16, Font.NORMAL, bluePDBI);
                Font regularTitle16Bold = new Font(fontName, 16, Font.NORMAL, BaseColor.BLACK);
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

                } catch (IOException | DocumentException e) {
                    e.printStackTrace();
                }



                onProgressUpdate(" items processing...");

                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.setSpacingBefore(20);
                try {
                    table.setWidths(new float[] {1f,2,3,2});
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
//                table.setHeaderRows(1);
//
//                table.setSplitRows(false);
                table.setComplete(false);

                PdfPCell headNo = new PdfPCell(new Phrase("No.",regularWhite));
                PdfPCell headJudul = new PdfPCell(new Phrase("Nama",regularWhite));
                PdfPCell headKategori = new PdfPCell(new Phrase("Id",regularWhite));
                PdfPCell headProvinsi = new PdfPCell(new Phrase("email",regularWhite));

                headNo.setPaddingLeft(10);
                headNo.setPaddingTop(5);
                headNo.setPaddingBottom(10);

                headJudul.setPaddingLeft(10);
                headJudul.setPaddingTop(5);
                headJudul.setPaddingBottom(10);

                headKategori.setPaddingLeft(10);
                headKategori.setPaddingTop(5);
                headKategori.setPaddingBottom(10);

                headProvinsi.setPaddingLeft(10);
                headProvinsi.setPaddingTop(5);
                headProvinsi.setPaddingBottom(10);

                headNo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headJudul.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headKategori.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headProvinsi.setVerticalAlignment(Element.ALIGN_MIDDLE);


                headNo.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                headJudul.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                headKategori.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                headProvinsi.setHorizontalAlignment(Element.ALIGN_MIDDLE);


//                headName.setPaddingTop(10);
//                headName.setPaddingBottom(14);

                headNo.setBackgroundColor(bluePDBI);
                headJudul.setBackgroundColor(bluePDBI);
                headKategori.setBackgroundColor(bluePDBI);
                headProvinsi.setBackgroundColor(bluePDBI);

                headNo.setBorder(Rectangle.NO_BORDER);
                headJudul.setBorder(Rectangle.NO_BORDER);
                headKategori.setBorder(Rectangle.NO_BORDER);
                headProvinsi.setBorder(Rectangle.NO_BORDER);

//                table.setComplete(true);
                table.addCell(headNo);
                table.addCell(headJudul);
                table.addCell(headKategori);
                table.addCell(headProvinsi);


                int n = 0;
                for (int aw=0;aw<arrayList1[0].size();aw++){
                    User user = arrayList1[0].get(aw);

                    onProgressUpdate(String.valueOf(aw)+" items processing...");

                    PdfPCell cellNo = new PdfPCell(new Phrase(String.valueOf(aw+1),regularSub));
                    PdfPCell cellNama = new PdfPCell(new Phrase(user.getNama_profile(),regularSub));
                    PdfPCell cellId = new PdfPCell(new Phrase(user.getUser_id(),regularSub));
                    PdfPCell cellEmail = new PdfPCell(new Phrase(user.getEmail_profile(),regularSub));


                    cellNo.setPaddingLeft(10);
                    cellNo.setPaddingTop(5);
                    cellNo.setPaddingBottom(10);

                    cellNama.setPaddingLeft(10);
                    cellNama.setPaddingTop(5);
                    cellNama.setPaddingBottom(10);

                    cellId.setPaddingLeft(10);
                    cellId.setPaddingTop(5);
                    cellId.setPaddingBottom(10);

                    cellEmail.setPaddingLeft(10);
                    cellEmail.setPaddingTop(5);
                    cellEmail.setPaddingBottom(10);


                    cellNo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellNama.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellId.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellEmail.setVerticalAlignment(Element.ALIGN_MIDDLE);


                    cellNo.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cellNama.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cellId.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cellEmail.setHorizontalAlignment(Element.ALIGN_MIDDLE);

                    cellNo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellNama.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellId.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellEmail.setVerticalAlignment(Element.ALIGN_MIDDLE);


                    cellNo.setBackgroundColor(bluePDBI);
                    cellNama.setBackgroundColor(bluePDBI);
                    cellId.setBackgroundColor(bluePDBI);
                    cellEmail.setBackgroundColor(bluePDBI);

                    cellNo.setBorder(Rectangle.NO_BORDER);
                    cellNama.setBorder(Rectangle.NO_BORDER);
                    cellId.setBorder(Rectangle.NO_BORDER);
                    cellEmail.setBorder(Rectangle.NO_BORDER);

                    if (aw%2==0){
                        cellNo.setBackgroundColor(BaseColor.WHITE);
                        cellNama.setBackgroundColor(BaseColor.WHITE);
                        cellId.setBackgroundColor(BaseColor.WHITE);
                        cellEmail.setBackgroundColor(BaseColor.WHITE);
                    }else {
                        cellNo.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cellNama.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cellId.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cellEmail.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    }

                    table.addCell(cellNo);
                    table.addCell(cellNama);
                    table.addCell(cellId);
                    table.addCell(cellEmail);
                }


                table.setComplete(true);

                try {
                    document.add(table);
                } catch (DocumentException ex) {
                    ex.printStackTrace();
                }

                PdfPTable ttd = new PdfPTable(2);
                ttd.setWidthPercentage(80);
                ttd.setSpacingBefore(60);

                PdfPCell space = new PdfPCell(new Phrase("", regularCategory));
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
            AdminPanel.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtUpdate.setText(a);
                }
            });
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

//            txtUpdate.setText(finish);
            check = txtUpdate.getText().toString();

            if (check.equals(finishing)){
                txtUpdate.setText(finish);
                dialogToShow.dismiss();
                Toast.makeText(AdminPanel.this, "Report berhasil dimuat", Toast.LENGTH_SHORT).show();

                printPDF();
            } else {
                Toast.makeText(AdminPanel.this, "Report gagal", Toast.LENGTH_SHORT).show();
                dialogToShow.dismiss();
            }

        }

        private void printPDF() {
            PrintManager printManager = (PrintManager)AdminPanel.this.getSystemService(Context.PRINT_SERVICE);
            try {
                PdfDocumentAdapter.printName = "User_Report_" + formattedDate;
                PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(AdminPanel.this,
                        Common.getAppPath(AdminPanel.this)+"User.pdf");
                Objects.requireNonNull(printManager).print("Document",printDocumentAdapter, new PrintAttributes.Builder().build());
            } catch (Exception ex){
                Log.e("ROCKMAN BARBERSHOP", ""+ex.getMessage());
            }
        }
    }

    private class AsyncMakePdfTransaksi extends AsyncTask<ArrayList<RiwayatTransaksi>,String, Integer> {

        androidx.appcompat.app.AlertDialog dialogToShow;
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AdminPanel.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.preloader_dialog,null);
        TextView txtUpdate = view.findViewById(R.id.txtUpdate);
        String data = "";

        @SuppressLint("WrongThread")
        @Override
        protected Integer doInBackground(ArrayList<RiwayatTransaksi>... arrayLists) {
            try {
                createPDFFile(Common.getAppPath(AdminPanel.this)+"RiwayatTransaksi.pdf", arrayLists);
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }

            return null;
        }

        private int createPDFFile(String path, ArrayList<RiwayatTransaksi>[] arrayList1) throws IOException, DocumentException{
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
                BaseColor bluePDBI = new BaseColor(27, 150, 170);//A = 1
                //            float fontSize = 12.0f;
                float valueFontSize = 12.0f;
                float fontSize = 12.0f;

                //Custom Font
                BaseFont fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

                Font regularHead = new Font(fontName, 14, Font.NORMAL, BaseColor.BLACK);
                Font regularWhite = new Font(fontName, 14, Font.NORMAL, BaseColor.WHITE);
                Font regularReport = new Font(fontName, 30, Font.BOLD, printAccent);
                Font regularTitle = new Font(fontName, 14, Font.NORMAL, BaseColor.BLACK);
                Font regularCategory = new Font(fontName, 12, Font.ITALIC, bluePDBI);
                Font regularSub = new Font(fontName, 12);
                Font regularTitle16 = new Font(fontName, 16, Font.NORMAL, BaseColor.BLACK);
                Font regularTitle16Blue = new Font(fontName, 16, Font.NORMAL, bluePDBI);
                Font regularTitle16Bold = new Font(fontName, 16, Font.NORMAL, BaseColor.BLACK);
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

                } catch (IOException | DocumentException e) {
                    e.printStackTrace();
                }


                //header
                onProgressUpdate(" items processing...");

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setSpacingBefore(20);
                try {
                    table.setWidths(new float[] {1f,3,2,2,2});
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
//                table.setHeaderRows(1);
//
//                table.setSplitRows(false);
                table.setComplete(false);

                PdfPCell headNo = new PdfPCell(new Phrase("No.",regularWhite));
                PdfPCell headJudul = new PdfPCell(new Phrase("Kode Transaksi",regularWhite));
                PdfPCell headKategori = new PdfPCell(new Phrase("User",regularWhite));
                PdfPCell headProvinsi = new PdfPCell(new Phrase("Harga",regularWhite));
                PdfPCell headDaerah = new PdfPCell(new Phrase("Tanggal",regularWhite));

                headNo.setPaddingLeft(10);
                headNo.setPaddingTop(5);
                headNo.setPaddingBottom(10);

                headJudul.setPaddingLeft(10);
                headJudul.setPaddingTop(5);
                headJudul.setPaddingBottom(10);

                headKategori.setPaddingLeft(10);
                headKategori.setPaddingTop(5);
                headKategori.setPaddingBottom(10);

                headProvinsi.setPaddingLeft(10);
                headProvinsi.setPaddingTop(5);
                headProvinsi.setPaddingBottom(10);

                headDaerah.setPaddingLeft(10);
                headDaerah.setPaddingTop(5);
                headDaerah.setPaddingBottom(10);

                headNo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headJudul.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headKategori.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headProvinsi.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headDaerah.setVerticalAlignment(Element.ALIGN_MIDDLE);

                headNo.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                headJudul.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                headKategori.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                headProvinsi.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                headDaerah.setHorizontalAlignment(Element.ALIGN_MIDDLE);


                headNo.setBackgroundColor(bluePDBI);
                headJudul.setBackgroundColor(bluePDBI);
                headKategori.setBackgroundColor(bluePDBI);
                headProvinsi.setBackgroundColor(bluePDBI);
                headDaerah.setBackgroundColor(bluePDBI);

                headNo.setBorder(Rectangle.NO_BORDER);
                headJudul.setBorder(Rectangle.NO_BORDER);
                headKategori.setBorder(Rectangle.NO_BORDER);
                headProvinsi.setBorder(Rectangle.NO_BORDER);
                headDaerah.setBorder(Rectangle.NO_BORDER);

//                table.setComplete(true);
                table.addCell(headNo);
                table.addCell(headJudul);
                table.addCell(headKategori);
                table.addCell(headProvinsi);
                table.addCell(headDaerah);


                int n = 0;
                for (int aw=0;aw<arrayList1[0].size();aw++){
                    RiwayatTransaksi produk = arrayList1[0].get(aw);

                    onProgressUpdate(String.valueOf(aw)+" items processing...");

                    PdfPCell cellNo = new PdfPCell(new Phrase(String.valueOf(aw+1),regularSub));
                    PdfPCell cellJudul = new PdfPCell(new Phrase(produk.getKode_transaksi(),regularSub));
                    PdfPCell cellKategori = new PdfPCell(new Phrase(produk.getUser_id(), regularSub));
                    PdfPCell cellProvinsi = new PdfPCell(new Phrase(String.valueOf(produk.getHarga()),regularSub));

                    PdfPCell cellDaerah = new PdfPCell(new Phrase(String.valueOf(produk.getTanggal_transaksi()),regularSub));

                    cellNo.setPaddingLeft(10);
                    cellNo.setPaddingTop(5);
                    cellNo.setPaddingBottom(10);

                    cellJudul.setPaddingLeft(10);
                    cellJudul.setPaddingTop(5);
                    cellJudul.setPaddingBottom(10);

                    cellKategori.setPaddingLeft(10);
                    cellKategori.setPaddingTop(5);
                    cellKategori.setPaddingBottom(10);

                    cellProvinsi.setPaddingLeft(10);
                    cellProvinsi.setPaddingTop(5);
                    cellProvinsi.setPaddingBottom(10);

                    cellDaerah.setPaddingLeft(10);
                    cellDaerah.setPaddingTop(5);
                    cellDaerah.setPaddingBottom(10);

                    cellNo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellJudul.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellKategori.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellProvinsi.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellDaerah.setVerticalAlignment(Element.ALIGN_MIDDLE);

                    cellNo.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cellJudul.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cellKategori.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cellProvinsi.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cellDaerah.setHorizontalAlignment(Element.ALIGN_MIDDLE);

                    cellNo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellJudul.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellKategori.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellProvinsi.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellDaerah.setVerticalAlignment(Element.ALIGN_MIDDLE);

                    cellNo.setBackgroundColor(bluePDBI);
                    cellJudul.setBackgroundColor(bluePDBI);
                    cellKategori.setBackgroundColor(bluePDBI);
                    cellProvinsi.setBackgroundColor(bluePDBI);
                    cellDaerah.setBackgroundColor(bluePDBI);

                    cellNo.setBorder(Rectangle.NO_BORDER);
                    cellJudul.setBorder(Rectangle.NO_BORDER);
                    cellKategori.setBorder(Rectangle.NO_BORDER);
                    cellProvinsi.setBorder(Rectangle.NO_BORDER);
                    cellDaerah.setBorder(Rectangle.NO_BORDER);

                    if (aw%2==0){
                        cellNo.setBackgroundColor(BaseColor.WHITE);
                        cellJudul.setBackgroundColor(BaseColor.WHITE);
                        cellKategori.setBackgroundColor(BaseColor.WHITE);
                        cellProvinsi.setBackgroundColor(BaseColor.WHITE);
                        cellDaerah.setBackgroundColor(BaseColor.WHITE);
                    }else {
                        cellNo.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cellJudul.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cellKategori.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cellProvinsi.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cellDaerah.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    }

                    table.addCell(cellNo);
                    table.addCell(cellJudul);
                    table.addCell(cellKategori);
                    table.addCell(cellProvinsi);
                    table.addCell(cellDaerah);
                }


                table.setComplete(true);

                try {
                    document.add(table);
                } catch (DocumentException ex) {
                    ex.printStackTrace();
                }

                PdfPTable ttd = new PdfPTable(2);
                ttd.setWidthPercentage(80);
                ttd.setSpacingBefore(60);

                PdfPCell space = new PdfPCell(new Phrase("", regularCategory));
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
            AdminPanel.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtUpdate.setText(a);
                }
            });
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

//            txtUpdate.setText(finish);
            check = txtUpdate.getText().toString();

            if (check.equals(finishing)){
                txtUpdate.setText(finish);
                dialogToShow.dismiss();
                Toast.makeText(AdminPanel.this, "Report berhasil dimuat", Toast.LENGTH_SHORT).show();

                printPDF();
            } else {
                Toast.makeText(AdminPanel.this, "Report gagal", Toast.LENGTH_SHORT).show();
                dialogToShow.dismiss();
            }

        }

        private void printPDF() {
            PrintManager printManager = (PrintManager)AdminPanel.this.getSystemService(Context.PRINT_SERVICE);
            try {
                PdfDocumentAdapter.printName = "RiwayatTransaksi_Report_" + formattedDate;
                PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(AdminPanel.this,
                        Common.getAppPath(AdminPanel.this)+"RiwayatTransaksi.pdf");
                Objects.requireNonNull(printManager).print("Document",printDocumentAdapter, new PrintAttributes.Builder().build());
            } catch (Exception ex){
                Log.e("ROCKMAN BARBERSHOP", ""+ex.getMessage());
            }
        }
    }


    private class AsyncMakePdfAntrian extends AsyncTask<ArrayList<Antrian>,String, Integer> {

        androidx.appcompat.app.AlertDialog dialogToShow;
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AdminPanel.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.preloader_dialog,null);
        TextView txtUpdate = view.findViewById(R.id.txtUpdate);
        String data = "";

        @SuppressLint("WrongThread")
        @Override
        protected Integer doInBackground(ArrayList<Antrian>... arrayLists) {
            try {
                createPDFFile(Common.getAppPath(AdminPanel.this)+"Antrian.pdf", arrayLists);
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }

            return null;
        }

        private int createPDFFile(String path, ArrayList<Antrian>[] arrayList1) throws IOException, DocumentException{
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
                BaseColor bluePDBI = new BaseColor(27, 150, 170);//A = 1
                //            float fontSize = 12.0f;
                float valueFontSize = 12.0f;
                float fontSize = 12.0f;

                //Custom Font
                BaseFont fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

                Font regularHead = new Font(fontName, 14, Font.NORMAL, BaseColor.BLACK);
                Font regularWhite = new Font(fontName, 14, Font.NORMAL, BaseColor.WHITE);
                Font regularReport = new Font(fontName, 30, Font.BOLD, printAccent);
                Font regularTitle = new Font(fontName, 14, Font.NORMAL, BaseColor.BLACK);
                Font regularCategory = new Font(fontName, 12, Font.ITALIC, bluePDBI);
                Font regularSub = new Font(fontName, 12);
                Font regularTitle16 = new Font(fontName, 16, Font.NORMAL, BaseColor.BLACK);
                Font regularTitle16Blue = new Font(fontName, 16, Font.NORMAL, bluePDBI);
                Font regularTitle16Bold = new Font(fontName, 16, Font.NORMAL, BaseColor.BLACK);
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

                } catch (IOException | DocumentException e) {
                    e.printStackTrace();
                }

                //header
                
                onProgressUpdate(" items processing...");

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setSpacingBefore(20);
                try {
                    table.setWidths(new float[] {1f,3,2,2,2});
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
//                table.setHeaderRows(1);
//
//                table.setSplitRows(false);
                table.setComplete(false);

                PdfPCell headNo = new PdfPCell(new Phrase("No.",regularWhite));
                PdfPCell headJudul = new PdfPCell(new Phrase("Kode Antrian",regularWhite));
                PdfPCell headKategori = new PdfPCell(new Phrase("Nama User",regularWhite));
                PdfPCell headProvinsi = new PdfPCell(new Phrase("Status Antrian",regularWhite));
                PdfPCell headDaerah = new PdfPCell(new Phrase("Tanggal Antrian",regularWhite));

                headNo.setPaddingLeft(10);
                headNo.setPaddingTop(5);
                headNo.setPaddingBottom(10);

                headJudul.setPaddingLeft(10);
                headJudul.setPaddingTop(5);
                headJudul.setPaddingBottom(10);

                headKategori.setPaddingLeft(10);
                headKategori.setPaddingTop(5);
                headKategori.setPaddingBottom(10);

                headProvinsi.setPaddingLeft(10);
                headProvinsi.setPaddingTop(5);
                headProvinsi.setPaddingBottom(10);

                headDaerah.setPaddingLeft(10);
                headDaerah.setPaddingTop(5);
                headDaerah.setPaddingBottom(10);

                headNo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headJudul.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headKategori.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headProvinsi.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headDaerah.setVerticalAlignment(Element.ALIGN_MIDDLE);

                headNo.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                headJudul.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                headKategori.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                headProvinsi.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                headDaerah.setHorizontalAlignment(Element.ALIGN_MIDDLE);


                headNo.setBackgroundColor(bluePDBI);
                headJudul.setBackgroundColor(bluePDBI);
                headKategori.setBackgroundColor(bluePDBI);
                headProvinsi.setBackgroundColor(bluePDBI);
                headDaerah.setBackgroundColor(bluePDBI);

                headNo.setBorder(Rectangle.NO_BORDER);
                headJudul.setBorder(Rectangle.NO_BORDER);
                headKategori.setBorder(Rectangle.NO_BORDER);
                headProvinsi.setBorder(Rectangle.NO_BORDER);
                headDaerah.setBorder(Rectangle.NO_BORDER);

//                table.setComplete(true);
                table.addCell(headNo);
                table.addCell(headJudul);
                table.addCell(headKategori);
                table.addCell(headProvinsi);
                table.addCell(headDaerah);


                int n = 0;
                for (int aw=0;aw<arrayList1[0].size();aw++){
                    Antrian produk = arrayList1[0].get(aw);

                    onProgressUpdate(String.valueOf(aw)+" items processing...");

                    PdfPCell cellNo = new PdfPCell(new Phrase(String.valueOf(aw+1),regularSub));
                    PdfPCell cellJudul = new PdfPCell(new Phrase(produk.getKode_antrian(),regularSub));
                    PdfPCell cellKategori = new PdfPCell(new Phrase(produk.getNama_user(), regularSub));
                    PdfPCell cellProvinsi = new PdfPCell(new Phrase(String.valueOf(produk.getStatus()),regularSub));

                    PdfPCell cellDaerah = new PdfPCell(new Phrase(String.valueOf(produk.getTanggal_antrian()),regularSub));

                    cellNo.setPaddingLeft(10);
                    cellNo.setPaddingTop(5);
                    cellNo.setPaddingBottom(10);

                    cellJudul.setPaddingLeft(10);
                    cellJudul.setPaddingTop(5);
                    cellJudul.setPaddingBottom(10);

                    cellKategori.setPaddingLeft(10);
                    cellKategori.setPaddingTop(5);
                    cellKategori.setPaddingBottom(10);

                    cellProvinsi.setPaddingLeft(10);
                    cellProvinsi.setPaddingTop(5);
                    cellProvinsi.setPaddingBottom(10);

                    cellDaerah.setPaddingLeft(10);
                    cellDaerah.setPaddingTop(5);
                    cellDaerah.setPaddingBottom(10);

                    cellNo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellJudul.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellKategori.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellProvinsi.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellDaerah.setVerticalAlignment(Element.ALIGN_MIDDLE);

                    cellNo.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cellJudul.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cellKategori.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cellProvinsi.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cellDaerah.setHorizontalAlignment(Element.ALIGN_MIDDLE);

                    cellNo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellJudul.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellKategori.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellProvinsi.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellDaerah.setVerticalAlignment(Element.ALIGN_MIDDLE);

                    cellNo.setBackgroundColor(bluePDBI);
                    cellJudul.setBackgroundColor(bluePDBI);
                    cellKategori.setBackgroundColor(bluePDBI);
                    cellProvinsi.setBackgroundColor(bluePDBI);
                    cellDaerah.setBackgroundColor(bluePDBI);

                    cellNo.setBorder(Rectangle.NO_BORDER);
                    cellJudul.setBorder(Rectangle.NO_BORDER);
                    cellKategori.setBorder(Rectangle.NO_BORDER);
                    cellProvinsi.setBorder(Rectangle.NO_BORDER);
                    cellDaerah.setBorder(Rectangle.NO_BORDER);

                    if (aw%2==0){
                        cellNo.setBackgroundColor(BaseColor.WHITE);
                        cellJudul.setBackgroundColor(BaseColor.WHITE);
                        cellKategori.setBackgroundColor(BaseColor.WHITE);
                        cellProvinsi.setBackgroundColor(BaseColor.WHITE);
                        cellDaerah.setBackgroundColor(BaseColor.WHITE);
                    }else {
                        cellNo.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cellJudul.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cellKategori.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cellProvinsi.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cellDaerah.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    }

                    table.addCell(cellNo);
                    table.addCell(cellJudul);
                    table.addCell(cellKategori);
                    table.addCell(cellProvinsi);
                    table.addCell(cellDaerah);
                }


                table.setComplete(true);

                try {
                    document.add(table);
                } catch (DocumentException ex) {
                    ex.printStackTrace();
                }


                PdfPTable ttd = new PdfPTable(2);
                ttd.setWidthPercentage(80);
                ttd.setSpacingBefore(60);

                PdfPCell space = new PdfPCell(new Phrase("", regularCategory));
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
            AdminPanel.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtUpdate.setText(a);
                }
            });
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

//            txtUpdate.setText(finish);
            check = txtUpdate.getText().toString();

            if (check.equals(finishing)){
                txtUpdate.setText(finish);
                dialogToShow.dismiss();
                Toast.makeText(AdminPanel.this, "Report berhasil dimuat", Toast.LENGTH_SHORT).show();

                printPDF();
            } else {
                Toast.makeText(AdminPanel.this, "Report gagal", Toast.LENGTH_SHORT).show();
                dialogToShow.dismiss();
            }

        }

        private void printPDF() {
            PrintManager printManager = (PrintManager)AdminPanel.this.getSystemService(Context.PRINT_SERVICE);
            try {
                PdfDocumentAdapter.printName = "Antrian_Report_" + formattedDate;
                PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(AdminPanel.this,
                        Common.getAppPath(AdminPanel.this)+"Antrian.pdf");
                Objects.requireNonNull(printManager).print("Document",printDocumentAdapter, new PrintAttributes.Builder().build());
            } catch (Exception ex){
                Log.e("ROCKMAN BARBERSHOP", ""+ex.getMessage());
            }
        }
    }




}
