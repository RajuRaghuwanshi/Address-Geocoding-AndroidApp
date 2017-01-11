package com.popuparea;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class GeoActivity extends ListActivity {
	private static final String TAG = "GeoActivity";
    private static final int MAX_ADDRESSES = 30;
    static SQLiteDatabase db;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addlist);
        try{
            db=SQLiteDatabase.openDatabase("/sdcard/Locations",null, SQLiteDatabase.CREATE_IF_NECESSARY);
           }catch(Exception e)
           {
        	   Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();	
           }
            /* try{
             db.execSQL("create table if not exists location(address text(200) Primary Key)");
             }
             catch(Exception e)
             {Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();	}*/
    }
    public void onLookupLocationClick(View view)
    {
        
        {
            EditText addressText = (EditText) findViewById(R.id.enterLocationValue);
            
            try
            {
                List<Address> addressList = new Geocoder(this).getFromLocationName(addressText.getText().toString(), MAX_ADDRESSES);
                
                List<AddressWrapper> addressWrapperList = new ArrayList<AddressWrapper>();
                
                for (Address address : addressList)
                {
                    addressWrapperList.add(new AddressWrapper(address));
                }
                
                setListAdapter(new ArrayAdapter<AddressWrapper>(this, android.R.layout.simple_list_item_single_choice, addressWrapperList));
            }
            catch (IOException e)
            {
                Log.e(TAG, "Could not geocode address", e);
                
                new AlertDialog.Builder(this)
                    .setMessage("some error occurs")
                    .setTitle("Message")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    }).show();
            }
        }
    }

    public void onOkClick(View view)
    {
        ListView listView = getListView();
        
        Intent intent = getIntent();
        if (listView.getCheckedItemPosition() != ListView.INVALID_POSITION)
        {
            AddressWrapper addressWrapper = (AddressWrapper)listView.getItemAtPosition(listView.getCheckedItemPosition());
        /*    new AlertDialog.Builder(this)
            .setMessage(addressWrapper.toString()+","+addressWrapper.getAddress().getLatitude()+","+addressWrapper.getAddress().getLongitude())
            .setTitle("Message")
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            }).show();*/
            
            db.execSQL("insert into location (address)values('"+addressWrapper.toString()+"')");	
			Toast.makeText(getApplicationContext(),"Location Saved",Toast.LENGTH_LONG).show();	
			Intent i=new Intent(getApplicationContext(), PopUpAreaActivity.class);
			startActivity(i);
           /* intent.putExtra("name", addressWrapper.toString());
            intent.putExtra("latitude", addressWrapper.getAddress().getLatitude());
            intent.putExtra("longitude", addressWrapper.getAddress().getLongitude());
        */
        }
        
        //this.setResult(RESULT_OK, intent);
       // finish();
    }

    private static class AddressWrapper
    {
        private Address address;

        public AddressWrapper(Address address)
        {
            this.address = address;
        }

        @Override
        public String toString()
        {
            StringBuilder stringBuilder = new StringBuilder();
            
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
            {
                stringBuilder.append(address.getAddressLine(i));
                
                if ((i + 1) < address.getMaxAddressLineIndex())
                {
                    stringBuilder.append(", ");
                }
            }
            
            return stringBuilder.toString();
        }

        public Address getAddress()
        {
            return address;
        }
    }

}
