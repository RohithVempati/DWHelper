package com.rohith.dwhelper;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class DwWebServices extends AppCompatActivity {
    RequestQueue requestQueue;
    EditText editId, editProvision, editRate;
    Button btnAdd, btnFind, btnEdit, btnDelete;
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        editId = findViewById(R.id.editId);
        editProvision = findViewById(R.id.editProvision);
        editRate = findViewById(R.id.editRate);
        btnAdd = findViewById(R.id.btnAdd);
        btnFind = findViewById(R.id.btnFind);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        status = findViewById(R.id.status);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecuteService("https://rohithvempati.com/DWDBv2/insert_product.php");
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findProvision("https://rohithvempati.com/DWDBv2/find_products.php?id=" + editId.getText().toString() + "");
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecuteService("https://rohithvempati.com/DWDBv2/edit_products.php");

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeletionService("https://rohithvempati.com/DWDBv2/delete_products.php");
            }
        });
    }

    private void ExecuteService (String URL){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(), "Operation Successful", Toast.LENGTH_SHORT).show();
                    status.setText(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("id", editId.getText().toString());
                    parameters.put("pname", editProvision.getText().toString());
                    parameters.put("rate", editRate.getText().toString());
                    return parameters;

                }
            };
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }

        private void DeletionService (String URL){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(), "Item Removed", Toast.LENGTH_SHORT).show();
                    ClearFields();
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
                    parameters.put("id", editId.getText().toString());
                    parameters.put("pname", editProvision.getText().toString());
                    parameters.put("rate", editRate.getText().toString());
                    return parameters;

                }
            };
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }

        private void ClearFields () {
            editId.setText("");
            editProvision.setText("");
            editRate.setText("");
            status.setText("Deleted Successfully!");
        }

        private void findProvision (String URL){
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    JSONObject jsonObject = null;
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            jsonObject = response.getJSONObject(i);
                            editProvision.setText(jsonObject.getString("pname"));
                            editRate.setText(jsonObject.getString("rate"));
                            status.setText("Details of the item with id " + jsonObject.getString("id") + " are:");
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Does not Exist", Toast.LENGTH_SHORT).show();
                    status.setText("Item does not exist!");
                    editProvision.setText("");
                    editRate.setText("");
                }
            }
            );
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonArrayRequest);

        }

}
