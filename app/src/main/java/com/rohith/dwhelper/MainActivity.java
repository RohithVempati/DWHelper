package com.rohith.dwhelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
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
import java.util.Map;
import java.util.SimpleTimeZone;


public class MainActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    TextView txtid,txtPname,txtRate,txtid2,txtPname2,txtRate2,txtid3,txtPname3,txtRate3,txtStatus,textView,textView2,textView3;
    EditText editPname,editId2,editDate,editQuantity;
    Button btnSearch,btnUpdate,btnEdit2,btnInfo,btnGetPdf;
    int[] curr_pos = new int[3];
    int reslength;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editPname= findViewById(R.id.editPname);
        txtid = findViewById(R.id.txtid);
        txtPname = findViewById(R.id.txtPname);
        txtRate = findViewById(R.id.txtRate);
        txtid2 = findViewById(R.id.txtid2);
        txtPname2 = findViewById(R.id.txtPname2);
        txtRate2 = findViewById(R.id.txtRate2);
        txtid3 = findViewById(R.id.txtid3);
        txtPname3 = findViewById(R.id.txtPname3);
        txtRate3 = findViewById(R.id.txtRate3);
        btnSearch=findViewById(R.id.btnSearch);
        editDate=findViewById(R.id.editDate);
        editId2=findViewById(R.id.editId2);
        editQuantity=findViewById(R.id.editQuantity);
        btnUpdate=findViewById(R.id.btnUpdate);
        btnEdit2=findViewById(R.id.btnEdit2);
        btnInfo=findViewById(R.id.btnInfo);
        txtStatus=findViewById(R.id.txtStatus);
        textView=findViewById(R.id.textView);
        textView2=findViewById(R.id.textView2);
        textView3=findViewById(R.id.textView3);
        btnGetPdf=findViewById(R.id.btnGetPdf);


            Button btnManage = findViewById(R.id.btnManage);
                btnManage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mngActivity = new Intent(MainActivity.this,DwWebServices.class);
                    startActivity(mngActivity);
                }
            });

            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setHeads(1);
                    clearFields();
                    SearchProvision("https://rohithvempati.com/DWDBv2/search_products.php?pname="+editPname.getText(),0);
                }
            });

            Button btnPrev = findViewById(R.id.btnPrev);
            Button btnNext = findViewById(R.id.btnNext);
            btnPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(curr_pos[1]>0) {
                        curr_pos[1] =curr_pos[1]-3;
                        clearFields();
                        if(curr_pos[2]==0) {
                            SearchProvision("https://rohithvempati.com/DWDBv2/search_products.php?pname=" + editPname.getText(), curr_pos[1]);
                        }
                        if(curr_pos[2]==1) {
                            GetInfo("https://rohithvempati.com/DWDBv2/get_info.php?date=" + editDate.getText(), curr_pos[1]);
                        }

                    }
                }
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(txtPname3.getText()!="") {
                        if (curr_pos[0] == 0) {
                            curr_pos[0] = 3;
                        }
                    }
                    if(curr_pos[1]<curr_pos[0]-2)
                        curr_pos[1] = curr_pos[1] + 3;
                        clearFields();
                        if(curr_pos[2]==0) {
                            SearchProvision("https://rohithvempati.com/DWDBv2/search_products.php?pname=" + editPname.getText(), curr_pos[1]);
                        }
                        if(curr_pos[2]==1) {
                            GetInfo("https://rohithvempati.com/DWDBv2/get_info.php?date=" + editDate.getText(), curr_pos[1]);
                        }



                }
            });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddToList("https://rohithvempati.com/DWDBv2/add_table.php");
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHeads(0);
                clearFields();
                GetInfo("https://rohithvempati.com/DWDBv2/get_info.php?date="+editDate.getText(),0);
            }
        });

        btnEdit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddToList("https://rohithvempati.com/DWDBv2/edit_table.php");
            }
        });

        btnGetPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent atActivity = new Intent(MainActivity.this,Attendance.class);
                startActivity(atActivity);
                Attendance.Date= editDate.getText();
            }
        });

        txtPname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editDate.getText().toString().trim().equals(""))
                    editDate.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(System.currentTimeMillis()));
                editId2.setText(txtid.getText());
                editQuantity.requestFocus();
            }
        });

        txtPname2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editDate.getText().toString().trim().equals(""))
                    editDate.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(System.currentTimeMillis()));
                editId2.setText(txtid2.getText());
                System.out.println(editDate.getText().toString());
                editQuantity.requestFocus();
            }
        });

        txtPname3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editDate.getText().toString().trim().equals(""))
                    editDate.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(System.currentTimeMillis()));
                editId2.setText(txtid3.getText());
                editQuantity.requestFocus();
            }
        });


    }

    private void clearFields(){
        txtid.setText("");
        txtPname.setText("");
        txtRate.setText("");
        txtid2.setText("");
        txtPname2.setText("");
        txtRate2.setText("");
        txtid3.setText("");
        txtPname3.setText("");
        txtRate3.setText("");

    }
    private void setHeads(int i){
        if(i==0){
            textView3.setText("Quantity");
        }
        if(i==1){
            textView3.setText("Rate");
        }
    }

    private int[] SearchProvision (String URL,int k){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(k);
                        txtid.setText(jsonObject.getString("id"));
                        txtRate.setText(jsonObject.getString("rate"));
                        txtPname.setText(jsonObject.getString("pname"));
                        JSONObject jsonObject1 = response.getJSONObject(k+1);
                        txtid2.setText(jsonObject1.getString("id"));
                        txtRate2.setText(jsonObject1.getString("rate"));
                        txtPname2.setText(jsonObject1.getString("pname"));
                        JSONObject jsonObject2 = response.getJSONObject(k+2);
                        txtid3.setText(jsonObject2.getString("id"));
                        txtRate3.setText(jsonObject2.getString("rate"));
                        txtPname3.setText(jsonObject2.getString("pname"));
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"No more similar items", Toast.LENGTH_SHORT).show();
                    }
                    reslength = response.length();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
        curr_pos[0]=reslength;
        curr_pos[1]=k;
        curr_pos[2]=0;
        return curr_pos;

    }

    private void AddToList (String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Operation Successful", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", editId2.getText().toString());
                parameters.put("quantity", editQuantity.getText().toString());
                parameters.put("date", editDate.getText().toString());
                return parameters;

            }
        };
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private int[] GetInfo(String URL,int k){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject jsonObject = response.getJSONObject(k);
                    txtid.setText(jsonObject.getString("id"));
                    txtRate.setText(jsonObject.getString("quantity"));
                    txtPname.setText(jsonObject.getString("pname"));
                    JSONObject jsonObject1 = response.getJSONObject(k+1);
                    txtid2.setText(jsonObject1.getString("id"));
                    txtRate2.setText(jsonObject1.getString("quantity"));
                    txtPname2.setText(jsonObject1.getString("pname"));
                    JSONObject jsonObject2 = response.getJSONObject(k+2);
                    txtid3.setText(jsonObject2.getString("id"));
                    txtRate3.setText(jsonObject2.getString("quantity"));
                    txtPname3.setText(jsonObject2.getString("pname"));
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_SHORT).show();
                }
                reslength = response.length();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
        curr_pos[0]=reslength;
        curr_pos[1]=k;
        curr_pos[2]=1;
        return curr_pos;

    }
}