import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class OpenRecent {

    private Preferences pref;
    private int MAX;

    public OpenRecent(int MAX) {
        this.MAX = MAX;
        pref = Preferences.userRoot().node(this.getClass().getName());
        pref.putInt("None", MAX);
    }

    //This method takes a String parameter that has to be the path to a file. It then adds it to the Preferences with a value of 1.
    public void addRecent(String url) throws BackingStoreException {
            pref.putInt(url, 0);
            String[] keys = pref.keys();
            for(int i = 0; i < keys.length; i++) {
                int temp = pref.getInt(keys[i], MAX);
                if(temp<MAX) {
                    temp++;
                    pref.putInt(keys[i], temp);
                } else {
                    pref.remove(keys[i]);
                }
        }
    }

    //This returns a String array of size MAX with all the recently opened in order.
    public String[] getRecents() throws BackingStoreException {
        String[] recents = new String[MAX];
        String[] keys = pref.keys();
        for(String item : keys) {
            int value = pref.getInt(item, 0) -1;
            if(value > -1 && value < MAX) {
                recents[value] = item;
            }
        }
        return recents;
    }
}
