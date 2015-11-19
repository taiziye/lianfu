package com.tangpo.lianfu.utils;

import com.tangpo.lianfu.R;

/**
 * Created by shengshoubo on 2015/11/5.
 */
public class GetBitmap {
    public static int getBitmap(int i) {
        switch (i) {
            case 0:
                return R.drawable.icon_marka;
            case 1:
                return R.drawable.icon_markb;
            case 2:
                return R.drawable.icon_markc;
            case 3:
                return R.drawable.icon_markd;
            case 4:
                return R.drawable.icon_marke;
            case 5:
                return R.drawable.icon_markf;
            case 6:
                return R.drawable.icon_markg;
            case 7:
                return R.drawable.icon_markh;
            case 8:
                return R.drawable.icon_marki;
            case 9:
                return R.drawable.icon_markj;
            default:
                return R.drawable.icon_gcoding;
        }
    }
}
