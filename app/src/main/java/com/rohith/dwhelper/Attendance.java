package com.rohith.dwhelper;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import java.util.HashMap;

public class Attendance extends AppCompatActivity {

    public static Editable Date;
    RequestQueue requestQueue;
    Button btnGetPdfGo,btnSetPresent;
    EditText editDate2,editStrength;
    Double eexp1=0.0,eexp2=0.0,eexp3=0.0,eexp4=0.0,eexp5=0.0,eexptotal=0.0,eexptotalbr=0.0;
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
        String[] spinItems = new String[]{"V-VII", "VIII-X","INTER", "STAFF I", "STAFF II"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinItems);
        spinner.setAdapter(adapter);
        btnGetPdfGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 30) {
                    if (Environment.isExternalStorageManager()) {
                        try {
                            FillInfo("https://rohithvempati.com/DWDBv2/fetch_table.php?date=" + editDate2.getText());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        catch(NullPointerException ex){
                            Toast.makeText(getApplicationContext(),"Please Set Attendance!!",Toast.LENGTH_SHORT).show();
                        }
                    } else { //request for the permission
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }
                if (Build.VERSION.SDK_INT <= 29 && Build.VERSION.SDK_INT>= 23){
                    checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,101);
                    try {
                        FillInfo("https://rohithvempati.com/DWDBv2/fetch_table.php?date=" + editDate2.getText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    catch(NullPointerException ex){
                        Toast.makeText(getApplicationContext(),"Please Set Attendance!!",Toast.LENGTH_SHORT).show();
                    }

                }
                if(Build.VERSION.SDK_INT<23){
                    try {
                        FillInfo("https://rohithvempati.com/DWDBv2/fetch_table.php?date=" + editDate2.getText());
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                    catch(NullPointerException ex){
                        Toast.makeText(getApplicationContext(),"Please Set Attendance!!",Toast.LENGTH_SHORT).show();
                    }
                }

            }

        });

        btnSetPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.put((String) spinner.getSelectedItem(),Integer.parseInt(String.valueOf(editStrength.getText())));
                int sp = spinner.getSelectedItemPosition()+1;
                Toast.makeText(getApplicationContext(),"Attendance Set Successfully!",Toast.LENGTH_SHORT).show();
                if(sp<5) {
                    spinner.setSelection(sp);
                }
                else {
                    spinner.setSelection(0);
                }
                editStrength.setText("");

            }
        });



    }
    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(Attendance.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(Attendance.this, new String[] { permission }, requestCode);
        }
    }

    private void NextFuncss() throws  ActivityNotFoundException{
        String mfile = editDate2.getText().toString();
        String mfilepath ="/storage/emulated/0/DWHelper/" + mfile + ".pdf";
        File file = new File(mfilepath);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(FileProvider.getUriForFile(this, getApplicationContext().getPackageName()+".provider",file),"application/pdf");
        target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(target, "Open File");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        }
        catch (
                ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Install PDF Viewer", Toast.LENGTH_SHORT).show();
            // Instruct the user to install a PDF reader here, or something
        }
    }

    private void FillInfo(String URL) throws IOException,NullPointerException {

        Document document = new Document();
        String mfile = editDate2.getText().toString();
        String mfilepath ="/storage/emulated/0/DWHelper/" + mfile + ".pdf";
        File path = new File("/storage/emulated/0/DWHelper");
        File file = new File(path, mfile + ".pdf");
        try {
            if(path.mkdir()){
                System.out.println("Path Created!");
            }
            else{
                path.mkdirs();
                System.out.println("Path nott created");
            }
            System.out.println(file.createNewFile());
        } catch (IOException e) {
            e.printStackTrace();
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
        Integer present3 = present.get("INTER");
        Integer present4 = present.get("STAFF I");
        Integer present5 = present.get("STAFF II");
        eexp1 = Math.round(present1 *27.66*100.0)/100.0;
        eexp2 = Math.round(present2 *32.66*100.0)/100.0;
        eexp3 = Math.round(present3 *46*100.0)/100.0;
        eexp4 = Math.round(present4 *32.66*100.0)/100.0;
        eexp5 = Math.round(present5 *46*100.0)/100.0;
        eexptotalbr = eexp1+eexp2+eexp3+eexp4+eexp5;
        eexptotal = Math.round(eexptotalbr*100)/100.0;
        double attTotal = present.get("V-VII")+present.get("VIII-X")+present.get("STAFF I")+present.get("STAFF II")+present.get("INTER");


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
                table.addCell(new Phrase("", fontH2));
                PdfPCell Subt = new PdfPCell(new Phrase("Sub Total",fontH1));
                Subt.setHorizontalAlignment(Element.ALIGN_CENTER);
                Subt.setVerticalAlignment(Element.ALIGN_MIDDLE);
                Subt.setColspan(3);
                table.addCell(Subt);
                table.addCell(new Phrase(String.valueOf(total1), fontH2));
                table.addCell(new Phrase("", fontH1));
                table.addCell(Subt);
                table.addCell(new Phrase(String.valueOf(total2), fontH2));

                PdfPTable table2 = new PdfPTable(new float[]{40, 40});
                table2.setSpacingBefore(10);
                table2.setSpacingAfter(10);
                table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
                table2.addCell(new Phrase("GRAND TOTAL", fontH1));
                String roundedgrandtotal = String.valueOf(Math.round(grandtotal * 100.0) / 100.0);
                table2.addCell(new Phrase(roundedgrandtotal, fontH1));
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
                table4.addCell(new PdfPCell(new Phrase("", fontH1))).setRowspan(4);
                table4.addCell(new PdfPCell(new Phrase("", fontH1))).setRowspan(4);
                table4.addCell(new PdfPCell(new Phrase("\n \n \n", fontH1))).setRowspan(4);
                table4.addCell(new PdfPCell(new Phrase("", fontH1))).setRowspan(4);
                table4.addCell(new PdfPCell(new Phrase("", fontH1))).setRowspan(4);

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
                PdfPCell gtotal = new PdfPCell(new Phrase(roundedgrandtotal, fontH1));
                gtotal.setRowspan(2);
                gtotal.setHorizontalAlignment(Element.ALIGN_CENTER);
                gtotal.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table5.addCell(gtotal);
                table5.addCell(new Phrase("1", fontH2));
                table5.addCell(new Phrase("V-VII", fontH2));
                table5.addCell(new Phrase(String.valueOf(present.get("V-VII")), fontH2));
                table5.addCell(new Phrase("27.66", fontH2));
                table5.addCell(new Phrase(String.valueOf(eexp1), fontH2));
                table5.addCell(new Phrase("2", fontH2));
                table5.addCell(new Phrase("VIII-X", fontH2));
                table5.addCell(new Phrase(String.valueOf(present.get("VIII-X")), fontH2));
                table5.addCell(new Phrase("32.66", fontH2));
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
                table5.addCell(new Phrase("INTER", fontH2));
                table5.addCell(new Phrase(String.valueOf(present.get("INTER")), fontH2));
                table5.addCell(new Phrase("46", fontH2));
                table5.addCell(new Phrase(String.valueOf(eexp3), fontH2));

                table5.addCell(new Phrase("4", fontH2));
                table5.addCell(new Phrase("STAFF I", fontH2));
                table5.addCell(new Phrase(String.valueOf(present.get("STAFF I")), fontH2));
                table5.addCell(new Phrase("32.66", fontH2));
                table5.addCell(new Phrase(String.valueOf(eexp4), fontH2));

                PdfPCell percap = new PdfPCell(new Phrase("PER-CAPITA", fontH1));
                percap.setRowspan(3);
                percap.setHorizontalAlignment(Element.ALIGN_CENTER);
                percap.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table5.addCell(percap);
                PdfPCell percapf = new PdfPCell(new Phrase(String.valueOf(percapita), fontH1));
                percapf.setRowspan(3);
                percapf.setHorizontalAlignment(Element.ALIGN_CENTER);
                percapf.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table5.addCell(percapf);

                table5.addCell(new Phrase("5", fontH2));
                table5.addCell(new Phrase("STAFF II", fontH2));
                table5.addCell(new Phrase(String.valueOf(present.get("STAFF II")), fontH2));
                table5.addCell(new Phrase("46", fontH2));
                table5.addCell(new Phrase(String.valueOf(eexp4), fontH2));

                PdfPCell tot = new PdfPCell(new Phrase("TOTAL", fontH1));
                tot.setHorizontalAlignment(Element.ALIGN_CENTER);
                tot.setVerticalAlignment(Element.ALIGN_MIDDLE);
                tot.setColspan(2);
                table5.addCell(tot);
                table5.addCell(new Phrase(String.valueOf(attTotal), fontH2));
                table5.addCell(new Phrase("", fontH2));
                table5.addCell(new Phrase(String.valueOf(eexptotal), fontH2));

                Paragraph paragraph = new Paragraph("DEPUTY WARDEN                                           PRINCIPAL");
                paragraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.setSpacingBefore(140);


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
                    System.out.println("OPening");
                    NextFuncss();


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
    }
}