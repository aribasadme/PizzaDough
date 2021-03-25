package net.ddns.aribas.pizzadough;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SavedRecipeItem {
    private final String mFileName;
    private final Calendar mDateSaved;

    public SavedRecipeItem(String fileName, Calendar date) {
        this.mFileName = fileName;
        this.mDateSaved = date;
    }

    public  String getFileName() {
        return mFileName;
    }

    public String getDateSaved() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm", Locale.getDefault());
        return sdf.format(mDateSaved.getTime());
    }
}
