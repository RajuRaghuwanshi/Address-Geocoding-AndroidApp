package com.popuparea;



import java.util.List;
import java.util.Locale;

 
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PopUpAreaActivity extends Activity {
    /** Called when the activity is first created. */
	Button B;
	EditText T;
	 SQLiteDatabase db;
	 ListView L;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        T=(EditText)findViewById(R.id.editText1);
        B=(Button)findViewById(R.id.button1);
        B.setOnClickListener(new LocateArea());
      
        
        LocationManager LM=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        LM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0,new LocationUpdate());
    
		try{
		 db=SQLiteDatabase.openDatabase("/sdcard/Locations",null,SQLiteDatabase.CREATE_IF_NECESSARY);
						
		}catch(Exception e){
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
		try{
            db.execSQL("create table if not exists location(address text(200) Primary Key)");
            }
            catch(Exception e)
            {Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();	}
		ArrayAdapter <String> adp=getNames();
	 L=(ListView)findViewById(R.id.listView1);
		
		
		L.setOnItemClickListener(new ListClick(this));
		L.setAdapter(adp);
	}
	
	
	class ListClick implements OnItemClickListener
	{ Context c;
		ListClick(Context c)
		{
			this.c=c;
			
		}
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int index,
				long arg3) {
			// TODO Auto-generated method stub
			//Toast.makeText(getApplicationContext(),L.getItemAtPosition(index)+"", Toast.LENGTH_LONG).show();
		  String name=L.getItemAtPosition(index).toString();
		 
	 String ph="";
		  Cursor C=db.rawQuery("select * from location where address='"+name+"'", null);
			if(C.moveToNext())
			{ph=C.getString(0);
			 AlertDialog.Builder ad=new AlertDialog.Builder(c);
			 ad.setTitle("Delete Address");
			 ad.setMessage(ph);
			 ad.setPositiveButton("Delete Confrim", new OkOnClickListener(c,ph));
			       
			  ad.show();
			
			
			}
		  
		}
		
		
	}
	
	private final class OkOnClickListener implements
	  DialogInterface.OnClickListener {
		 Context c;
		   String Ph;
		   OkOnClickListener(Context c,String Ph)
			{
				this.c=c;
				this.Ph=Ph;
			}
	  public void onClick(DialogInterface dialog, int which) {
		  db.execSQL("delete  from location where address='"+Ph.trim()+"'");
		  
		  Log.e("++++++++", "======"+Ph);
		  ArrayAdapter <String> adp=getNames();
			 
			 L.setAdapter(adp);
	  }
	} 
    class LocateArea implements OnClickListener
    {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent i=new Intent(getApplicationContext(), GeoActivity.class);
			startActivity(i);
		}}
    
  /* class ClickDialogPhone implements OnClickListener
	{  Context c;
	   String Ph;
	   ClickDialogPhone(Context c,String Ph)
		{
			this.c=c;
			this.Ph=Ph;
		}
	
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), Ph, Toast.LENGTH_SHORT).show();
			db.rawQuery("delete  from location where address='"+name+"'", null);
		}
		
	}*/
    
    class LocationUpdate implements LocationListener
    {

		@Override
		public void onLocationChanged(Location loc) {
			// TODO Auto-generated method stub
			double lat=loc.getLatitude();
			double log=loc.getLongitude();
			 getAddressLocation(lat,log);
		//	displayMap(lat,log);
		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}}
    
    void getAddressLocation(double lat,double log)
    {try{
    	Geocoder gc=new Geocoder(this,Locale.getDefault());
    	List <Address> address=gc.getFromLocation(lat,log,1);
    	if(address.size()>0)
    	{   StringBuffer sb=new StringBuffer();
    	 	Address add=address.get(0);
    	 	for(int i=0;i<add.getMaxAddressLineIndex();i++)
    	 	{
    	 	 sb.append(add.getAddressLine(i)+" ");	    	 		
    	 	}
    	 	String address1=sb.toString();
        	String a=new String();
        		for(int i=0;i<address1.length();i++)
        		{ char c=address1.charAt(i);
        			
        			if(c==',')
        		{a=a+",";
        			
        		}
        		else
        		{a=a+c;}
        		}
        		address1=a;
        		String adds=address1;
    		Cursor C=db.rawQuery("select * from location where address like '%"+adds.trim()+"%'", null);
 			if(C.moveToNext())
 			{
    	 	
 				Toast.makeText(getApplicationContext(),sb.toString(),Toast.LENGTH_LONG).show();
 	    	 	 playDefaultNotificationSound();
    	 	boolean st=sb.toString().contains(C.getString(0));
    	 	 if(st)
    	 	 {
    	 	
    		Toast.makeText(getApplicationContext(),sb.toString(),Toast.LENGTH_LONG).show();
    	 	 playDefaultNotificationSound();
    	 	 T.setText("");
    	 	 }
    		}
    	 	
    	}
    	 
    	
    }catch(Exception e)
    {
   		Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_LONG).show();
   	    	
    }
    }
    
    private void playDefaultNotificationSound() {
    	Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    	Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
    	r.play();
    }
    
    
//get names from table
	
	ArrayAdapter <String> getNames()
	{
		
		Cursor rec=db.rawQuery("select * from location",null);
	  String s[]=new String[rec.getCount()];
	  int i=0;
	while(rec.moveToNext())
	{s[i]=rec.getString(0);
	Log.e("Data Base",rec.getString(0) );
	i++;	
		}
		
	ArrayAdapter <String>adp=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,s); 	
		return(adp);
		
	}
    
    
    
    
}