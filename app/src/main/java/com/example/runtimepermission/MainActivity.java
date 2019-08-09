package com.example.runtimepermission;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{

    ListView listView;
    String[] items;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);/*type casting*/
        /*statement for if permission already granted*/
        searchView=(SearchView)findViewById(R.id.search);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(MainActivity.this, "You have already granted this permission!",
                    Toast.LENGTH_SHORT).show();
            runtimePermission();/*this statement i have used only for my cell phone.if i will  connect someones other cell phone then
                                  i will remove this line*/
        }
        else
            {
                runtimePermission();/*calling the functin name runtime permission in onCreate method*/
            }

    }


    public void runtimePermission()          /*This is one function by a name runtimePermission*/
    {
        Dexter.withActivity(this)           /*This is a kind of method which is ritten inside a function */
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        display();/*permission granted ke baad run time permission ke andar call kiya gya hai display function*/


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }


                }).check();


    }

    /*now after getting permision create a method for listing out the song in the List View */
    /*creating an array list of songs and storing it in arrayList array array. */
    public ArrayList<File>findSong(File file){             /*another class with name findSong*/

        ArrayList<File>arrayList=new ArrayList<>();
        File[] files=file.listFiles();

        for(File singleFile: files)
        {
            if(singleFile.isDirectory()&&!singleFile.isHidden()){
                arrayList.addAll(findSong(singleFile));
            }
            else
            {
                if(singleFile.getName().endsWith(".mp3")||
                        singleFile.getName().endsWith(".wav")){
                    arrayList.add(singleFile);
                }
            }
        }

        return arrayList;        /*This class is returning the arrayList */


    }

    /*now we will create a function for display.*/
    /*first we will create a string[] array of items and declare it*/

    /*this display code act as a (Boiler plates) for finding any kind of  file from the External storage*/
    /*if we need .txt file then replace .mp3 with .txt and all the txt file will be set into one location i.e on ListView*/

    void display()
    {

        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());   /*now here the arrayList is displayed in a list view and it is connected with this display function by findSong()*/
        items = new String[mySongs.size()];

        for (int i = 0; i < mySongs.size(); i++) {    /*isme check krega ki list ke naam me jaha jaha .mp3 ya .wav
                                                         dikhega usko ye space se replace kr dega and jab show hoga toh naam me mp3 ya
                                                         wap likha hua nhi dikhega app me */
            items[i] = mySongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");
        }
        /*Adopter-it is always used when we use list view beacaue of this adopter only we are able to show content i list*/
        /*ArrayAdopter is used to View the list view of items*/
        final ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(myAdapter);   /*Here by perormin this code,the .mp3 and .wav file from the cellphone will be displayed on
                                          the ListView UI*/


       /*Above we have passed the music file in the ListView.Now we will see that what happens if the iten in the listView is clicked */
        /*crating another method, if an item in an itemlist is clicked then which new activity should display. */
        /*we are creating this method inside the void display function*/




        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {     /*method for search view*/
            @Override
            public boolean onQueryTextSubmit(String s) {    /*this method submit the text enter into the search option*/
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                myAdapter.getFilter().filter(s);  /*isse kya hoga ki jobhi hum search view me enter krenge woh mere
                                                  listview(myAdopter me search krega and "s "hai jo hum search view me quary dalenge*/
                return false;
            }
        });





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName =listView.getItemAtPosition(i).toString();

                /*intent krna means pass krna*/
                /*here  in actual scence we are connecting main.java(list wala) with playersActivity.java (buttons or UI )*/

                /*.putExtra means, " songs" is a key and mySong is the value.....so .putExtra will pass the info to another activity i.e PlayersActivity*/
                startActivity(new Intent(getApplicationContext(),PlayersActivity.class)
                        .putExtra("songs",mySongs)
                        .putExtra("songname",songName)
                        .putExtra("pos",i));

                        /*three info will be passed to other activity i.e mySongs,songName and position of song in array i.e "i"
                        and they woill be related  with there key value */


            }
        });




    }




}
