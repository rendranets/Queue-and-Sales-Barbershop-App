package com.dicoding.rockman_barbershop.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dicoding.rockman_barbershop.Activity.AdminPanel;
import com.dicoding.rockman_barbershop.Activity.DetailBarangActivity;
import com.dicoding.rockman_barbershop.Activity.HeaderFooter;
import com.dicoding.rockman_barbershop.Activity.ProfileEditActivity;
import com.dicoding.rockman_barbershop.R;
import com.dicoding.rockman_barbershop.report.Common;
import com.dicoding.rockman_barbershop.report.PdfDocumentAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ProfileFragment extends Fragment {
    public static boolean reloadNeed;
    private TextView fullName,email,phone, lencanaView, adminPanel;
    private View lineAdminPanel;
    private String Sname, Sphone, Semail, Simage, Stipe, stringLencana, lencana;
    int Slencana;

    private String finish = "Finished", finishing = "Finishing up...", check, stringCount;
    private Boolean succes = false;
    private String formattedDate;

    private int countLencana;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;

    private static final int EDIT_CODE = 10;
    PdfWriter writer;

    private Button logout;
    private ImageView edit, download;
    private String userId;
    private ProgressDialog loading;
    private ProgressBar progressBar;

    private CircleImageView profileImage;
    private Uri profileImageUri = null;

    Bitmap compressedImageFile;
    MediaPlayer mediaPlayer;
    Button regbot;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mediaPlayer= MediaPlayer.create(getActivity(), R.raw.logout);

        phone       = v.findViewById(R.id.profile_phone_user);
        fullName    = v.findViewById(R.id.profile_name_user);
        email       = v.findViewById(R.id.profile_email_user);
        progressBar = v.findViewById(R.id.progressBar);
        profileImage= v.findViewById(R.id.profile_image);
        adminPanel = v.findViewById(R.id.admin_panel);
        lineAdminPanel = v.findViewById(R.id.line_admin_panel);
        download  = (ImageView) v.findViewById(R.id.report_profile);

        adminPanel.setVisibility(View.GONE);
        lineAdminPanel.setVisibility(View.GONE);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userId = fAuth.getCurrentUser().getUid();

        loading = ProgressDialog.show(getActivity(),
                null,
                "Mohon Tunggu",
                true,
                false);

        logout = v.findViewById(R.id.logout_btn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Yakin ingin keluar?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();//logout
                                mediaPlayer.start();
                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container,
                                        new AskToLoginFragment()).commit();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
            }
        });

        edit = v.findViewById(R.id.edit_profil);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileEditActivity.class);

                intent.putExtra(ProfileEditActivity.ExtraImage, Simage);
                intent.putExtra(ProfileEditActivity.ExtraName, Sname);
                intent.putExtra(ProfileEditActivity.ExtraPhone, Sphone);
                startActivity(intent);

            }
        });

      adminPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdminPanel.class);
                startActivity(intent);
            }
        });
        return v;
    }

    private void setProfile(){
        DocumentReference documentReference = fStore.document("users/" + userId);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            /** Memasukan data ke TextView **/

                            Simage = documentSnapshot.getString("picture_url_profile");

                            Sname = documentSnapshot.getString("nama_profile");
                            Semail = documentSnapshot.getString("email_profile");
                            Sphone = documentSnapshot.getString("phone_profile");
//                            Slencana = documentSnapshot.getLong("total_entry").intValue();
                            Stipe = documentSnapshot.getString("tipe");

                            fullName.setText(Sname);
                            email.setText(Semail);
                            phone.setText(Sphone);
//                            descLencana(Slencana);
//                            lencana = stringLencana+"("+Slencana+")";

                            try {
                                Glide.with(getActivity())
                                        .load(Simage)
                                        .placeholder(R.drawable.profile_update)
                                        .into(profileImage);

//                                setLencana();
                            } catch (RuntimeException e){
                                e.printStackTrace();
                            }
                            permission();
                            if (Stipe.equals("admin")){
                                adminPanel.setVisibility(View.VISIBLE);
                                lineAdminPanel.setVisibility(View.VISIBLE);
                            }
//                            progressBar.setVisibility(View.GONE);
                            loading.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Document Tidak Ada", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Document Error", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void permission(){
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AsyncMakePdf1 asyncMakePdf1=new AsyncMakePdf1();
                                asyncMakePdf1.execute();
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

    private class AsyncMakePdf1 extends AsyncTask<String,String ,Integer> {

        androidx.appcompat.app.AlertDialog dialogToShow;
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.preloader_dialog,null);
        TextView txtUpdate = view.findViewById(R.id.txtUpdate);

        @Override
        protected Integer doInBackground(String... string) {
            try {
                createPDFFile(Common.getAppPath(getActivity())+"Profile.pdf");
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }

            return null;
        }

        private int createPDFFile(String path) throws IOException, DocumentException{

            onProgressUpdate("createPDFFile");
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
                document.addAuthor("PDBI");
                document.addCreator("SOBAT BUDAYA");

                //Font Setting
                BaseColor colorAccent = new BaseColor(0, 153, 204, 255);
                BaseColor printAccent = new BaseColor(216, 27, 96);
                BaseColor bluePDBI = new BaseColor(27, 150, 241);//A = 1            float fontSize = 12.0f;
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

                PdfPCell footerName = new PdfPCell(new Phrase("Sobat Budaya", footerN));
                PdfPCell footerEmail = new PdfPCell(new Phrase("budaya-indonesia.org", footerE));

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

                } catch (IOException | DocumentException e) {
                    e.printStackTrace();
                }

                //header

                PdfPTable tableHeading = new PdfPTable(2);
                tableHeading.setWidthPercentage(100);
                tableHeading.setSpacingBefore(50);

                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                formattedDate = df.format(c);


                PdfPCell preSpace = new PdfPCell(new Phrase("", regularCategory));
                PdfPCell preName = new PdfPCell(new Phrase(Sname, regularTitle));
                PdfPCell preDate = new PdfPCell(new Phrase("DATE: " + formattedDate, regularTitle));
                PdfPCell preBill = new PdfPCell(new Phrase("No : 0001", regularTitle));

                preBill.setVerticalAlignment(Element.ALIGN_BOTTOM);
                preBill.setHorizontalAlignment(Element.ALIGN_RIGHT);

                preDate.setVerticalAlignment(Element.ALIGN_BOTTOM);
                preDate.setHorizontalAlignment(Element.ALIGN_RIGHT);
                preName.setBorder(Rectangle.NO_BORDER);
                preSpace.setBorder(Rectangle.NO_BORDER);
                preDate.setBorder(Rectangle.NO_BORDER);
                preBill.setBorder(Rectangle.NO_BORDER);

                try {
                    tableHeading.addCell(preSpace);
                    tableHeading.addCell(preBill);
                    tableHeading.addCell(preName);
                    tableHeading.addCell(preDate);
                    document.add(tableHeading);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                onProgressUpdate(" items processing...");

                //Image profile

//                try {

                PdfPTable tableImageProfile = new PdfPTable(1);
                tableImageProfile.setWidthPercentage(30);
                tableImageProfile.setSpacingBefore(50);

                Image image = null;
                if (Simage != null){
                    onProgressUpdate(" items image processing");
                    image = Image.getInstance(Simage);
                } else {
                    Drawable d = getResources().getDrawable(R.drawable.userphoto);
                    BitmapDrawable bitDw = ((BitmapDrawable) d);
                    Bitmap bmp = bitDw.getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    image = Image.getInstance(stream.toByteArray());
                }
                onProgressUpdate(" items image add to cell");
                PdfPCell preImageProfile = new PdfPCell(image, true);

                preImageProfile.setVerticalAlignment(Element.ALIGN_CENTER);
                preImageProfile.setHorizontalAlignment(Element.ALIGN_CENTER);

                preImageProfile.setBorder(Rectangle.NO_BORDER);
                onProgressUpdate(" items image add to table");

                tableImageProfile.addCell(preImageProfile);

                try {
                    document.add(tableImageProfile);
                } catch (DocumentException e) {
                    e.printStackTrace();
                    e.getMessage();
                }

//                    tableProfile.setComplete(true);


                //Isi

                PdfPTable tableProfile = new PdfPTable(2);
                tableProfile.setWidthPercentage(80);
                tableProfile.setSpacingBefore(50);
                tableProfile.setSpacingAfter(30);

                try {
                    tableProfile.setWidths(new float[] { 3,3});
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
//                tableProfile.setComplete(false);

                PdfPCell TitleBiodata = new PdfPCell(new Phrase("Biodata", regularTitle16));
                PdfPCell spaces = new PdfPCell(new Phrase("    ", regularWhite));

                PdfPCell preNameProfileTitle = new PdfPCell(new Phrase("Nama Kontributor", regularTitle16));
                PdfPCell preNameProfile = new PdfPCell(new Phrase(": " + Sname,regularTitle16));
                PdfPCell preEmailProfileTitle = new PdfPCell(new Phrase("Email Kontributor", regularTitle16));
                PdfPCell preEmailProfile = new PdfPCell(new Phrase(": " + Semail, regularTitle16));
                PdfPCell prePhoneProfileTitle = new PdfPCell(new Phrase("No.Telp Kontributor", regularTitle16));
                PdfPCell prePhoneProfile = new PdfPCell(new Phrase(": " + Sphone , regularTitle16));

                PdfPCell preContributionTitle = new PdfPCell(new Phrase("Konribusi", regularTitle16));
                PdfPCell preLencanaTitle = new PdfPCell(new Phrase("Lencana Kontributor", regularTitle16));
                PdfPCell preLencana = new PdfPCell(new Phrase(": " + stringLencana, regularTitle16));
                PdfPCell preTotalTitle = new PdfPCell(new Phrase("Total Kontribusi", regularTitle16));
                PdfPCell preTotal = new PdfPCell(new Phrase(": " + stringCount + " Entry", regularTitle16));
                PdfPCell preRegisterProfileTitle = new PdfPCell(new Phrase("Tanggal Regist", regularTitle16));
                PdfPCell preRegisterProfile = new PdfPCell(new Phrase(": " + "10 januari 2020", regularTitle16));
               //                preNameProfileTitle.setVerticalAlignment(Element.ALIGN_BOTTOM);
//                preNameProfileTitle.setHorizontalAlignment(Element.ALIGN_LEFT);
//
//                preNameProfile.setVerticalAlignment(Element.ALIGN_BOTTOM);
//                preNameProfile.setHorizontalAlignment(Element.ALIGN_LEFT);
//
//                preEmailProfileTitle.setVerticalAlignment(Element.ALIGN_BOTTOM);
//                preEmailProfileTitle.setHorizontalAlignment(Element.ALIGN_LEFT);
//
//                preEmailProfile.setVerticalAlignment(Element.ALIGN_BOTTOM);
//                preEmailProfile.setHorizontalAlignment(Element.ALIGN_LEFT);
//
//                prePhoneProfileTitle.setVerticalAlignment(Element.ALIGN_BOTTOM);
//                prePhoneProfileTitle.setHorizontalAlignment(Element.ALIGN_LEFT);
//
//                prePhoneProfile.setVerticalAlignment(Element.ALIGN_BOTTOM);
//                prePhoneProfile.setHorizontalAlignment(Element.ALIGN_LEFT);
//
//                preRegisterProfileTitle.setVerticalAlignment(Element.ALIGN_BOTTOM);
//                preRegisterProfileTitle.setHorizontalAlignment(Element.ALIGN_LEFT);
//
//                preRegisterProfile.setVerticalAlignment(Element.ALIGN_BOTTOM);
//                preRegisterProfile.setHorizontalAlignment(Element.ALIGN_LEFT);

                TitleBiodata.setBorder(Rectangle.NO_BORDER);
                spaces.setBorder(Rectangle.NO_BORDER);
                preNameProfileTitle.setBorder(Rectangle.NO_BORDER);
                preNameProfile.setBorder(Rectangle.NO_BORDER);
                preEmailProfileTitle.setBorder(Rectangle.NO_BORDER);
                preEmailProfile.setBorder(Rectangle.NO_BORDER);
                prePhoneProfileTitle.setBorder(Rectangle.NO_BORDER);
                prePhoneProfile.setBorder(Rectangle.NO_BORDER);
                preContributionTitle.setBorder(Rectangle.NO_BORDER);
                preLencanaTitle.setBorder(Rectangle.NO_BORDER);
                preLencana.setBorder(Rectangle.NO_BORDER);
                preTotalTitle.setBorder(Rectangle.NO_BORDER);
                preTotal.setBorder(Rectangle.NO_BORDER);
                preRegisterProfileTitle.setBorder(Rectangle.NO_BORDER);
                preRegisterProfile.setBorder(Rectangle.NO_BORDER);

                try {

                    tableProfile.addCell(TitleBiodata);
                    tableProfile.addCell(spaces);
                    tableProfile.addCell(spaces);
                    tableProfile.addCell(spaces);
                    tableProfile.addCell(preNameProfileTitle);
                    tableProfile.addCell(preNameProfile);
                    tableProfile.addCell(preEmailProfileTitle);
                    tableProfile.addCell(preEmailProfile);
                    tableProfile.addCell(prePhoneProfileTitle);
                    tableProfile.addCell(prePhoneProfile);

                    tableProfile.addCell(spaces);
                    tableProfile.addCell(spaces);

                    tableProfile.addCell(preContributionTitle);
                    tableProfile.addCell(spaces);
                    tableProfile.addCell(spaces);
                    tableProfile.addCell(spaces);
                    tableProfile.addCell(preLencanaTitle);
                    tableProfile.addCell(preLencana);
                    tableProfile.addCell(preTotalTitle);
                    tableProfile.addCell(preTotal);
                    tableProfile.addCell(preRegisterProfileTitle);
                    tableProfile.addCell(preRegisterProfile);

                    document.add(tableProfile);

                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                document.close();

                onProgressUpdate(finishing);


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
            getActivity().runOnUiThread(new Runnable() {
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
                printPDF();
                Toast.makeText(getActivity(), "Report berhasil dimuat", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getActivity(), "Report gagal", Toast.LENGTH_SHORT).show();
                dialogToShow.dismiss();
            }

        }

        private void printPDF() {
            PrintManager printManager = (PrintManager)getActivity().getSystemService(Context.PRINT_SERVICE);
            try {
                PdfDocumentAdapter.printName = "Profile_Report_" + formattedDate;
                PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(getActivity(),
                        Common.getAppPath(getActivity())+"Profile.pdf");
                Objects.requireNonNull(printManager).print("Document",printDocumentAdapter, new PrintAttributes.Builder().build());
            } catch (Exception ex){
                Log.e("SOBAT BUDAYA", ""+ex.getMessage());
            }
        }
    }
    
    
    @Override
    public void onStart() {
        super.onStart();
        setProfile();
    }

    @Override
    public void onStop() {
        super.onStop();
//        setProfile()
    }
}
