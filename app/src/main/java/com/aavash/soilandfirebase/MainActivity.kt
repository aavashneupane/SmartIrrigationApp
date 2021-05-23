package com.aavash.soilandfirebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationBuilderWithBuilderAccessor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {

//    val database = FirebaseDatabase.getInstance()
//    val myRef = database.getReference("FirebaseIOT")
    private lateinit var soilvalue:TextView
    private lateinit var pumpcondition:TextView
    private lateinit var images:ImageView

    lateinit var notificationManager:NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId="com.aavash.soilandfirebase"
    private val description="Test notification"

    override fun onCreate(savedInstanceState: Bundle?) {

        notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("FirebaseIOT/soilmoisture")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        soilvalue=findViewById(R.id.soilvalue)
        pumpcondition=findViewById(R.id.pumpcondition)
        images=findViewById(R.id.images)

        //to open notification
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent=PendingIntent.getActivity(this@MainActivity,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(Double::class.java)
                Log.d("Sensor data", "Value is: $value")



                if (value != null) {
                  //  val soils =value.toString()
                    soilvalue.setText(""+value);

                    //Toast.makeText(this@MainActivity, "Cuurent soil moisture value is $value", Toast.LENGTH_SHORT).show()

//


                    if(value<100){
                //        Toast.makeText(this@MainActivity, "Cuurent soil moisture value is $value", Toast.LENGTH_SHORT).show()
                        pumpcondition.setText("High moisture is detected so pump is not active.")
                        images.setBackgroundResource(R.drawable.done);



                    }else{
                  //      Toast.makeText(this@MainActivity, "Cuurent soil moisture value is $value", Toast.LENGTH_SHORT).show()
                        pumpcondition.setText("Low moisture is detected so pump has been activated.")

                        images.setBackgroundResource(R.drawable.farmergif1);

                        //for notification



                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            notificationChannel= NotificationChannel(channelId,description,NotificationManager.IMPORTANCE_HIGH)
                            notificationChannel.enableVibration(true)
                            notificationManager.createNotificationChannel(notificationChannel)

                            builder=Notification.Builder(this@MainActivity,channelId)
                                    .setContentTitle("Soil Moisture")
                                    .setContentText("Your soil is low on moisture ($value). Activating water pump.")
                                    .setSmallIcon(R.drawable.ic_launcher_round)
                                   // .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.mipmap.ic_launcher))
                                    .setContentIntent(pendingIntent)


                        }else
                        {
                            builder=Notification.Builder(this@MainActivity)
                                    .setContentTitle("Soil Moisture")
                                    .setContentText("Your soil is low on moisture ($value). Activating water pump.")
                                    .setSmallIcon(R.drawable.ic_launcher_round)
                                    //.setLargeIcon(BitmapFactory.decodeResource(this.resources,R.mipmap.ic_launcher))
                                    .setContentIntent(pendingIntent)

                        }
                        notificationManager.notify(1234,builder.build())


                    }

                }
            }



            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Sensor Temp", "Failed to read value.", error.toException())
                Toast.makeText(this@MainActivity, "Failed to read Value", Toast.LENGTH_SHORT).show()
            }
        })
    }




}


