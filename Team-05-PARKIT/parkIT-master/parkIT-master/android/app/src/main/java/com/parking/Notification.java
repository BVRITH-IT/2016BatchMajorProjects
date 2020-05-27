package com.parking;

import com.facebook.react.ReactActivity;

import java.net.URL;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.openide.util.NbBundle;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.HtmlBrowser;

class Notification extends Object {

    /** Module notification accepted */
    private static boolean accepted = false;

    /** The Notification dialog */
    private static java.awt.Dialog dialog = null;

    /** This class is a singleton */
    private Notification() {
    }

    /** Tests whether XML file contains notification tag. If so opens modal
     * dialog with the notification.
     * @return True if there was a notification, false if not.
     */

    static boolean performNotification( Updates updates, AutoupdateType at ) {

        final String text = updates.getNotificationText();
        final URL url = updates.getNotificationURL();

        if ( text == null ) {
            return false;
        }

        // impl #24786, remeber and check last showed notification
        Settings settings = Settings.getShared ();
        if (settings.getAcceptedNotifications () == null) {
            settings.setAcceptedNotifications (new HashMap ());
        }
        Map mapNotifications = settings.getAcceptedNotifications ();
        Integer lastNotificationId = (Integer)mapNotifications.get (new Integer (at.getName ().hashCode ()));
        if (lastNotificationId != null && lastNotificationId.intValue () == text.hashCode ()) {
            // message have been showed previously
            return false;
        } else {
            mapNotifications.put (new Integer (at.getName ().hashCode ()), new Integer (text.hashCode ()));
            settings.setAcceptedNotifications (mapNotifications);
        }

        final JButton closeButton = new JButton (
                getBundle("CTL_Notification_Close")
        );
        closeButton.getAccessibleContext ().setAccessibleName (getBundle("ACS_Notification_Close"));
        final JButton urlButton = new JButton (
                getBundle("CTL_Notification_URL")
        );
        urlButton.getAccessibleContext ().setAccessibleName (getBundle("ACS_Notification_URL"));

        JOptionPane pane = new JOptionPane (
                text,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION
        );

        pane.setOptions (new Object[] {});
        pane.getAccessibleContext ().setAccessibleName (getBundle( "ACS_Notification_Title" ));

        DialogDescriptor dd = new DialogDescriptor (
                pane,
                getBundle( "CTL_Notification_Title" ),
                true,
                DialogDescriptor.DEFAULT_OPTION,
                DialogDescriptor.OK_OPTION,
                new ActionListener () {
                    public void actionPerformed (ActionEvent ev) {
                                          /*
                                          dialog.setVisible (false);
                                          dialog.dispose ();
                                          dialog = null;
                                          */
                        if (ev.getSource () == urlButton ) {
                            // display www browser
                            if ( url != null ) {
                                javax.swing.SwingUtilities.invokeLater( new Runnable() {
                                    public void run() {
                                        HtmlBrowser.URLDisplayer.getDefault ().showURL( url );
                                    }
                                } );
                            }
                        }
                    }
                }
        );

        dd.setOptions( url != null ? new Object[] {closeButton, urlButton} :
                new Object[] {closeButton} );
        dd.setClosingOptions( null );
        dialog = DialogDisplayer.getDefault().createDialog( dd );
        dialog.show ();
        return true;
    }

    static boolean performModuleNotification( String text ) {

        if ( text == null ) {
            return false;
        }

        final JButton cancelButton = new JButton (
                getBundle("CTL_Notification_Cancel")
        );
        cancelButton.getAccessibleContext ().setAccessibleName (getBundle("ACS_Notification_Cancel"));

        final JButton okButton = new JButton (
                getBundle("CTL_Notification_OK")
        );
        okButton.getAccessibleContext ().setAccessibleName (getBundle("ACS_Notification_OK"));

        JOptionPane pane = new JOptionPane (
                text,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION
        );
        pane.getAccessibleContext ().setAccessibleName (getBundle( "ACS_Notification_Title" ));

        pane.setOptions (new Object[] {});

        DialogDescriptor dd = new DialogDescriptor (
                pane,
                getBundle( "CTL_Notification_Title" ),
                true,
                DialogDescriptor.DEFAULT_OPTION,
                DialogDescriptor.OK_OPTION,
                new ActionListener () {
                    public void actionPerformed (ActionEvent ev) {
                        if ( ev.getSource() == okButton )
                            accepted = true;
                        else
                            accepted = false;

                        dialog.setVisible( false );
                    }
                }
        );

        dd.setOptions( new Object[] {okButton, cancelButton} );
        dd.setClosingOptions( null );
        dialog = DialogDisplayer.getDefault().createDialog( dd );
        accepted = false;
        dialog.show();

        return accepted;
    }

    private static String getBundle( String key ) {
        return NbBundle.getMessage( Notification.class, key );
    }
}