package com.lichard49.keeperhead;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.content.Intent;
import android.content.LocusId;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChatHead();
            }
        });
    }

    private void startChatHead() {
        String id = "169";
        createNotificationChannel(id);

        // Create intent for starting the bubble activity
        Intent target = new Intent(this, BubbleActivity.class)
                .setAction(Intent.ACTION_VIEW);
        PendingIntent bubbleIntent =
                PendingIntent.getActivity(this, 2, target,
                        PendingIntent.FLAG_UPDATE_CURRENT /* flags */);

        // Setup dynamic shortcut from app icon
        Person person = new Person.Builder()
                .setBot(true)
                .setName("KeeperHead")
                .setIcon(Icon.createWithResource(this, R.drawable.ic_launcher_background))
                .setImportant(true)
                .build();

        Notification.MessagingStyle style = new Notification.MessagingStyle(person)
                .setGroupConversation(false)
                .addMessage("Tap to open KeeperHead", System.currentTimeMillis(), person);

        ShortcutInfo shortcutInfo =
                new ShortcutInfo.Builder(this, id)
                        .setLongLived(true)
                        .setShortLabel("KeeperHead")
                        .setIntent(target)
                        .setPerson(person)
                        .build();

        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        List<ShortcutInfo> shortcuts = new LinkedList<>();
        shortcuts.add(shortcutInfo);
        shortcutManager.addDynamicShortcuts(shortcuts);

        // Setup notification and enable launching bubble from notification
        Notification.BubbleMetadata bubbleData =
                new Notification.BubbleMetadata.Builder()
                        .setDesiredHeight(600)
                        .setIcon(Icon.createWithResource(this,
                                R.drawable.ic_launcher_background))
                        .setIntent(bubbleIntent)
                        .setAutoExpandBubble(true)
                        .setSuppressNotification(true)
                        .build();

        LocusId chatId = new LocusId(id);

        Notification.Builder builder =
                new Notification.Builder(this, id)
                        .setContentIntent(bubbleIntent)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setBubbleMetadata(bubbleData)
                        .addPerson(person)
                        .setStyle(style)
                        .setShortcutId(shortcutInfo.getId())
                        .setLocusId(chatId)
                        .setShowWhen(true)
                        .setCategory(Notification.CATEGORY_MESSAGE);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(0, builder.build());
    }

    private void createNotificationChannel(String id) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "KeeperHead";
            String description = "KeeperHead bubble";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            channel.setAllowBubbles(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
