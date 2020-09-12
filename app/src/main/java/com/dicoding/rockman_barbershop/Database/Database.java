//package com.dicoding.rockman_barbershop.Database;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteQueryBuilder;
//
//import com.dicoding.rockman_barbershop.model.Pesanan;
//import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Database extends SQLiteAssetHelper {
//
//    private static final String DB_NAME="rockman1.db";
//    private static final int DB_VER=1;
//    public Database(Context context) {
//        super(context, DB_NAME, null, DB_VER);
//    }
//
//    public List<Pesanan> getCarts(){
//        SQLiteDatabase db = getReadableDatabase();
//        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
//
//        String[] sqlSelect={"nama_barang","kode_barang","kuantitas","harga_barang"};
//        String sqlTable = "Pesanan";
//
//        qb.setTables(sqlTable);
//        Cursor c = qb.query(db,sqlSelect,null,null,null,null,null);
//
//        final List<Pesanan> result = new ArrayList<>();
//        if (c.moveToFirst())
//        {
//            do{
//                result.add(new Pesanan(c.getString(c.getColumnIndex("nama_barang")),
//                                c.getString(c.getColumnIndex("kode_barang")),
//                                c.getString(c.getColumnIndex("kuantitas")),
//                                c.getString(c.getColumnIndex("harga_barang"))
//                        ));
//            }while (c.moveToNext());
//        }
//        return result;
//    }
//
//    public void addToCart(Pesanan pesanan){
//        SQLiteDatabase db = getReadableDatabase();
//        String query = String.format("INSERT INTO Pesanan(nama_barang,kode_barang,kuantitas,harga_barang)" +
//                        "VALUES('%s','%s','%s','%s');",
//                pesanan.getNama_barang(),
//                pesanan.getKode_barang(),
//                pesanan.getKuantitas(),
//                pesanan.getHarga_barang());
//        db.execSQL(query);
//    }
//
//    public void cleanCart(){
//        SQLiteDatabase db = getReadableDatabase();
//        String query = String.format("DELETE FROM Pesanan");
//        db.execSQL(query);
//    }
//}
