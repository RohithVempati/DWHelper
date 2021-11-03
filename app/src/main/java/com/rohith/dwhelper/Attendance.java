package com.rohith.dwhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

public class Attendance extends AppCompatActivity {

    public static Editable Date;
    RequestQueue requestQueue;
    Button btnGetPdfGo,btnSetPresent;
    EditText editDate2,editStrength;
    Double eexp1=0.0,eexp2=0.0,eexp3=0.0,eexptotal=0.0;
    HashMap<String,Integer> present = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        btnGetPdfGo = findViewById(R.id.btnGetPdfGo);
        editDate2 = findViewById(R.id.editDate2);
        btnSetPresent=findViewById(R.id.btnSetPresent);
        editDate2.setText(Date);
        editStrength=findViewById(R.id.editStrength);
        Spinner spinner = findViewById(R.id.spinner);
        String[] spinItems = new String[]{"V-VII", "VIII-X", "STAFF"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinItems);
        spinner.setAdapter(adapter);
        btnGetPdfGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        String[] permit = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permit, 111);
                    } else {
                        try {
                            FillInfo("https://rohithvempati.com/DWDBv2/fetch_table.php?date=" + editDate2.getText());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        FillInfo("https://rohithvempati.com/DWDBv2/fetch_table.php?date=" + editDate2.getText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnSetPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.put((String) spinner.getSelectedItem(),Integer.parseInt(String.valueOf(editStrength.getText())));
                Toast.makeText(getApplicationContext(),"Attendance Set Successfully!",Toast.LENGTH_SHORT).show();
                editStrength.setText("");

            }
        });



    }

    private void FillInfo(String URL) throws IOException {

        Document document = new Document();
        String mfile = editDate2.getText().toString();
        String mfilepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/DWHelper/" + mfile + ".pdf";
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/DWHelper");
        File file = new File(path, mfile + ".pdf");
        Boolean isPresent = true;
        try {
            path.mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!path.exists()) {
            isPresent = path.mkdir();
        }
        if (isPresent) {
            file = new File(path.getAbsolutePath(), mfile + ".pdf");
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    String[] permit = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permit, 1000);
                } else {
                    try {
                        FillInfo("https://rohithvempati.com/DWDBv2/fetch_table.php?date=" + editDate2.getText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    FillInfo("https://rohithvempati.com/DWDBv2/fetch_table.php?date=" + editDate2.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        PdfPTable table = new PdfPTable(new float[]{4, 18, 6, 5, 5, 4, 18, 6, 5, 5});
        Font fontH1 = new Font();
        fontH1.setSize(7.5f);
        fontH1.isBold();
        Font fontH2 = new Font();
        fontH2.setSize(5);
        fontH2.isBold();
        Integer present1 = present.get("V-VII");
        Integer present2 = present.get("VIII-X");
        Integer present3 = present.get("STAFF");
        eexp1 = Math.round(present1 *28.759*100.0)/100.0;
        eexp2 = Math.round(present2 *33.931*100.0)/100.0;
        eexp3 = Math.round(present3 *33.931*100.0)/100.0;
        eexptotal = eexp1+eexp2+eexp3;
        double attTotal = present.get("V-VII")+present.get("VIII-X")+present.get("STAFF");


        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(new Phrase("Sl No", fontH2));
        table.addCell(new Phrase("NAME OF THE PROVISION", fontH2));
        table.addCell(new Phrase("QUANTITY \n (KGs)", fontH2));
        table.addCell(new Phrase("RATE", fontH2));
        table.addCell(new Phrase("AMOUNT", fontH2));
        table.addCell(new Phrase("Sl No", fontH2));
        table.addCell(new Phrase("NAME OF THE PROVISION", fontH2));
        table.addCell(new Phrase("QUANTITY \n (KGs)", fontH2));
        table.addCell(new Phrase("RATE", fontH2));
        table.addCell(new Phrase("AMOUNT", fontH2));

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                JSONObject jsonObject1 = null;
                JSONObject jsonObject2 = null;
                Double total1 = 0.0, total2 = 0.0, totallast = 0.0;
                for (int i = 0; i < response.length() / 2; i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        table.addCell(new Phrase(jsonObject.getString("id"), fontH1));
                        table.addCell(new Phrase(jsonObject.getString("pname"), fontH1));
                        table.addCell(new Phrase(jsonObject.getString("quantity"), fontH2));
                        table.addCell(new Phrase(jsonObject.getString("rate"), fontH2));
                        table.addCell(new Phrase(jsonObject.getString("amount"), fontH2));
                        System.out.println(jsonObject.getString("amount"));
                        if (!jsonObject.getString("amount").equals("")) {
                            total1 = Double.parseDouble(jsonObject.getString("amount")) + total1;
                            total1 = Math.round(total1 * 100.0) / 100.0;
                        }


                        jsonObject1 = response.getJSONObject(i + response.length() / 2);
                        table.addCell(new Phrase(jsonObject1.getString("id"), fontH1));
                        table.addCell(new Phrase(jsonObject1.getString("pname"), fontH1));
                        table.addCell(new Phrase(jsonObject1.getString("quantity"), fontH2));
                        table.addCell(new Phrase(jsonObject1.getString("rate"), fontH2));
                        table.addCell(new Phrase(jsonObject1.getString("amount"), fontH2));
                        if (!jsonObject1.getString("amount").equals("")) {
                            total2 = Double.parseDouble(jsonObject1.getString("amount")) + total2;
                            total2 = Math.round(total2 * 100.0) / 100.0;
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                Double grandtotal = total1 + total2;
                if (response.length() % 2 == 1) {
                    table.addCell(new Phrase("", fontH1));
                    table.addCell(new Phrase("", fontH1));
                    table.addCell(new Phrase("", fontH1));
                    table.addCell(new Phrase("", fontH1));
                    table.addCell(new Phrase("", fontH1));
                    try {
                        jsonObject2 = response.getJSONObject(response.length() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        table.addCell(new Phrase(jsonObject2.getString("id"), fontH1));
                        table.addCell(new Phrase(jsonObject2.getString("pname"), fontH1));
                        table.addCell(new Phrase(jsonObject2.getString("quantity"), fontH2));
                        table.addCell(new Phrase(jsonObject2.getString("rate"), fontH2));
                        table.addCell(new Phrase(jsonObject2.getString("amount"), fontH2));
                        if (!jsonObject2.getString("amount").equals("")) {
                            totallast = Double.parseDouble(jsonObject2.getString("amount")) + totallast;
                            totallast = Math.round(totallast * 100.0) / 100.0;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    grandtotal = grandtotal + totallast;
                    total2 = total2 + totallast;
                }
                double percapita = Math.round(grandtotal/attTotal*100.0)/100.0;
                table.addCell(new Phrase("TOTAL", fontH2));
                table.addCell(new Phrase("", fontH1));
                table.addCell(new Phrase("", fontH1));
                table.addCell(new Phrase("", fontH1));
                table.addCell(new Phrase(String.valueOf(total1), fontH2));
                table.addCell(new Phrase("TOTAL", fontH2));
                table.addCell(new Phrase("", fontH1));
                table.addCell(new Phrase("", fontH1));
                table.addCell(new Phrase("", fontH1));
                table.addCell(new Phrase(String.valueOf(total2), fontH2));

                PdfPTable table2 = new PdfPTable(new float[]{40, 40});
                table2.setSpacingBefore(10);
                table2.setSpacingAfter(10);
                table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
                table2.addCell(new Phrase("GRAND TOTAL", fontH1));
                table2.addCell(new Phrase(String.valueOf(grandtotal), fontH1));
                table2.setWidthPercentage(48);
                PdfPTable table0 = new PdfPTable(1);
                table0.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                table0.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
                table0.addCell(new Phrase("MJPTBCW RESIDENTIAL SCHOOL KARWAN & YAKUTPURA \n @KOMPALLY", fontH1));
                table0.addCell(new Phrase("DAILY ISSUE SHEET                    DATE: " + editDate2.getText(), fontH1));
                PdfPTable table3 = new PdfPTable(1);
                table3.setSpacingBefore(10);
                table3.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                table3.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
                table3.addCell(new Phrase("MENU", fontH1));
                table3.setWidthPercentage(48);
                PdfPTable table4 = new PdfPTable(new float[]{40, 40, 40, 40, 40});
                table4.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                table4.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
                table4.addCell(new Phrase("MILK TIME", fontH1));
                table4.addCell(new Phrase("TIFFIN", fontH1));
                table4.addCell(new Phrase("LUNCH", fontH1));
                table4.addCell(new Phrase("SNACKS", fontH1));
                table4.addCell(new Phrase("DINNER", fontH1));
                table4.addCell(new PdfPCell(new Phrase("", fontH1))).setRowspan(2);
                table4.addCell(new PdfPCell(new Phrase("", fontH1))).setRowspan(2);
                table4.addCell(new PdfPCell(new Phrase("\n \n \n", fontH1))).setRowspan(2);
                table4.addCell(new PdfPCell(new Phrase("", fontH1))).setRowspan(2);
                table4.addCell(new PdfPCell(new Phrase("", fontH1))).setRowspan(2);

                PdfPTable table5 = new PdfPTable(7);
                table5.setSpacingBefore(10);
                table5.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                table5.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
                table5.addCell(new Phrase("S No", fontH2));
                table5.addCell(new Phrase("CLASS", fontH2));
                table5.addCell(new Phrase("ATTENDANCE", fontH2));
                table5.addCell(new Phrase("Rs/-", fontH2));
                table5.addCell(new Phrase("ELIGIBLE EXPENDITURE", fontH2));
                PdfPCell exp = new PdfPCell(new Phrase("EXPENDITURE", fontH1));
                exp.setHorizontalAlignment(Element.ALIGN_CENTER);
                exp.setVerticalAlignment(Element.ALIGN_MIDDLE);
                exp.setRowspan(2);
                table5.addCell(exp);
                PdfPCell gtotal = new PdfPCell(new Phrase(String.valueOf(grandtotal), fontH1));
                gtotal.setRowspan(2);
                gtotal.setHorizontalAlignment(Element.ALIGN_CENTER);
                gtotal.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table5.addCell(gtotal);
                table5.addCell(new Phrase("1", fontH2));
                table5.addCell(new Phrase("V-VII", fontH2));
                table5.addCell(new Phrase(String.valueOf(present.get("V-VII")), fontH2));
                table5.addCell(new Phrase("28.759", fontH2));
                table5.addCell(new Phrase(String.valueOf(eexp1), fontH2));
                table5.addCell(new Phrase("2", fontH2));
                table5.addCell(new Phrase("VIII-X", fontH2));
                table5.addCell(new Phrase(String.valueOf(present.get("VIII-X")), fontH2));
                table5.addCell(new Phrase("33.931", fontH2));
                table5.addCell(new Phrase(String.valueOf(eexp2), fontH2));
                PdfPCell elig = new PdfPCell(new Phrase("ELIGIBLE EXPENDITURE", fontH1));
                elig.setHorizontalAlignment(Element.ALIGN_CENTER);
                elig.setVerticalAlignment(Element.ALIGN_MIDDLE);
                elig.setRowspan(2);
                table5.addCell(elig);
                PdfPCell eexpt = new PdfPCell(new Phrase(String.valueOf(eexptotal), fontH1));
                eexpt.setRowspan(2);
                eexpt.setHorizontalAlignment(Element.ALIGN_CENTER);
                eexpt.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table5.addCell(eexpt);
                table5.addCell(new Phrase("3", fontH2));
                table5.addCell(new Phrase("STAFF", fontH2));
                table5.addCell(new Phrase(String.valueOf(present.get("STAFF")), fontH2));
                table5.addCell(new Phrase("33.931", fontH2));
                table5.addCell(new Phrase(String.valueOf(eexp3), fontH2));

                PdfPCell tot = new PdfPCell(new Phrase("TOTAL", fontH1));
                tot.setHorizontalAlignment(Element.ALIGN_CENTER);
                tot.setVerticalAlignment(Element.ALIGN_MIDDLE);
                tot.setColspan(2);
                table5.addCell(tot);
                table5.addCell(new Phrase(String.valueOf(attTotal), fontH2));
                table5.addCell(new Phrase("", fontH2));
                table5.addCell(new Phrase(String.valueOf(eexptotal), fontH2));
                PdfPCell percap = new PdfPCell(new Phrase("PER-CAPITA", fontH1));
                percap.setHorizontalAlignment(Element.ALIGN_CENTER);
                percap.setVerticalAlignment(Element.ALIGN_MIDDLE);
                percap.setRowspan(2);
                table5.addCell(percap);
                PdfPCell percap3 = new PdfPCell(new Phrase(percapita +"\n", fontH1));
                percap3.setHorizontalAlignment(Element.ALIGN_CENTER);
                percap3.setVerticalAlignment(Element.ALIGN_MIDDLE);
                percap3.setRowspan(2);
                table5.addCell(percap3);
                Paragraph paragraph = new Paragraph("DEPUTY WARDEN                                           PRINCIPAL");
                paragraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.setSpacingBefore(120);


                try {
                    PdfWriter.getInstance(document, new FileOutputStream(mfilepath));
                    document.open();
                    document.add(table0);
                    document.add(table);
                    document.add(table2);
                    document.add(table3);
                    document.add(table4);
                    document.add(table5);
                    document.add(paragraph);
                    document.close();

                } catch (FileNotFoundException | DocumentException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
        Toast.makeText(getApplicationContext(), "PDF Stored in Documents/DWHelper", Toast.LENGTH_LONG).show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 111:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    try {
                        FillInfo("https://rohithvempati.com/DWDBv2/fetch_table.php?date="+editDate2.getText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                }
        }
    }
}