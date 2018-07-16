package uic.hcilab.citymeter;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import static android.graphics.Typeface.BOLD;

public class DataAnalysis {

    public int getColorOfSound(double sound){
        if (sound < 50) return Color.argb(100,0,228,0);
        else if (sound < 65) return Color.argb(100,255,255,0);
        else if (sound < 80) return Color.argb(100,255,126,0);
        else if (sound < 95) return Color.argb(100,255,0,0);
        else if (sound < 110) return Color.argb(100,143,63,151);
        else return Color.argb(100,126,0,35);
    }

    public int getColorOfPM(double pm25){
        if (pm25 < 50) return Color.argb(100,0,228,0);
        else if (pm25 < 101) return Color.argb(100,255,255,0);
        else if (pm25 < 151) return Color.argb(100,255,126,0);
        else if (pm25 < 201) return Color.argb(100,255,0,0);
        else if (pm25 < 301) return Color.argb(100,143,63,151);
        else return Color.argb(100,126,0,35);
    }

    public String getTextOfSound(double sound){
        if (sound < 50) return "Good";
        else if (sound < 65) return "Moderate";
        else if (sound < 80) return "Unhealthy for Sensitive Groups";
        else if (sound < 95) return "Unhealthy";
        else if (sound < 110) return "Very Unhealthy";
        else return "Hazardous";
    }

    public String getTextOfPM(double pm25){
        if (pm25 < 50) return "Good";
        else if (pm25 < 101) return "Moderate";
        else if (pm25 < 151) return "Unhealthy for Sensitive Groups";
        else if (pm25 < 201) return "Unhealthy";
        else if (pm25 < 301) return "Very Unhealthy";
        else return "Hazardous";
    }

    public double getPosOnBar(double value, int level[], int numOfLevel){
        int curIdx = -1;
        int prevStd = 0;
        for(int i=0; i< numOfLevel; i++){
            if(value < level[i]) {
                curIdx = i;
                break;
            }
            prevStd = level[i];
        }
        if(curIdx == -1)
            return numOfLevel;
        else
            return ((value - prevStd)/(level[curIdx]-prevStd)) + curIdx;

    }

    public CharSequence getStepsToDo(double pm25){
        CharSequence str;
        if (pm25 < 50) {
            SpannableString tmp1 = new SpannableString("It’s a great day to be active outside.\n\n");
            tmp1.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 0), 0, tmp1.length(), 0);
            tmp1.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp1.length(), 0);
            tmp1.setSpan(new RelativeSizeSpan(0.3f), tmp1.length()-1, tmp1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            str = tmp1;
        }
        else if (pm25 < 101) {

            SpannableString tmp1 = new SpannableString("Unusually sensitive people: \n");
            tmp1.setSpan(new StyleSpan(BOLD), 0, tmp1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tmp1.setSpan(new RelativeSizeSpan(1.2f), 0, tmp1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp2 = new SpannableString("Consider reducing prolonged or heavy exertion.\n\n");
            tmp2.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 0), 0, tmp2.length(), 0);
            tmp2.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp2.length(), 0);
            tmp2.setSpan(new RelativeSizeSpan(0.3f), tmp2.length()-1, tmp2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp3 = new SpannableString("Watch for symptoms such as coughing or shortness of breath.\n\n");
            tmp3.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 0), 0, tmp3.length(), 0);
            tmp3.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp3.length(), 0);
            tmp3.setSpan(new RelativeSizeSpan(0.3f), tmp3.length()-1, tmp3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp4 = new SpannableString("These are signs to take it easier.\n\n");
            tmp4.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 0), 0, tmp4.length(), 0);
            tmp4.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp4.length(), 0);
            tmp4.setSpan(new RelativeSizeSpan(0.3f), tmp4.length()-1, tmp4.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp5 = new SpannableString("Everyone else:\n");
            tmp5.setSpan(new StyleSpan(BOLD), 0, tmp5.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tmp5.setSpan(new RelativeSizeSpan(1.2f), 0, tmp5.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp6 = new SpannableString("It’s a good day to be active outside\n\n");
            tmp6.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 30), 0, tmp6.length(), 0);
            tmp6.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp6.length(), 0);
            tmp6.setSpan(new RelativeSizeSpan(0.3f), tmp6.length()-1, tmp6.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            str = TextUtils.concat(tmp1, tmp2, tmp3, tmp4, tmp5, tmp6);
        }

        else if (pm25 < 151){
            SpannableString tmp1 = new SpannableString("Sensitive groups: \n");
            tmp1.setSpan(new StyleSpan(BOLD), 0, tmp1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tmp1.setSpan(new RelativeSizeSpan(1.2f), 0, tmp1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp2 = new SpannableString("Reduce prolonged or heavy exertion.\n\n");
            tmp2.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 0), 0, tmp2.length(), 0);
            tmp2.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp2.length(), 0);
            tmp2.setSpan(new RelativeSizeSpan(0.3f), tmp2.length()-1, tmp2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp3 = new SpannableString("It’s OK to be active outside, but take more breaks and do less intense activities.\n\n");
            tmp3.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 0), 0, tmp3.length(), 0);
            tmp3.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp3.length(), 0);
            tmp3.setSpan(new RelativeSizeSpan(0.3f), tmp3.length()-1, tmp3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp4 = new SpannableString("Watch for symptoms such as coughing or shortness of breath.\n\n");
            tmp4.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 0), 0, tmp4.length(), 0);
            tmp4.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp4.length(), 0);
            tmp4.setSpan(new RelativeSizeSpan(0.3f), tmp4.length()-1, tmp4.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp5 = new SpannableString("People with asthma:\n");
            tmp5.setSpan(new StyleSpan(BOLD), 0, tmp5.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tmp5.setSpan(new RelativeSizeSpan(1.2f), 0, tmp5.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp6 = new SpannableString("Should follow their asthma action plans and keep quick relief medicine handy.\n\n");
            tmp6.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 30), 0, tmp6.length(), 0);
            tmp6.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp6.length(), 0);
            tmp6.setSpan(new RelativeSizeSpan(0.3f), tmp6.length()-1, tmp6.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            str = TextUtils.concat(tmp1, tmp2, tmp3, tmp4, tmp5, tmp6);

        }
        else if (pm25 < 201){
            SpannableString tmp1 = new SpannableString("Sensitive groups: \n");
            tmp1.setSpan(new StyleSpan(BOLD), 0, tmp1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tmp1.setSpan(new RelativeSizeSpan(1.2f), 0, tmp1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp2 = new SpannableString("Avoid prolonged or heavy exertion.\n\n");
            tmp2.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 0), 0, tmp2.length(), 0);
            tmp2.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp2.length(), 0);
            tmp2.setSpan(new RelativeSizeSpan(0.3f), tmp2.length()-1, tmp2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp3 = new SpannableString("Consider moving activities indoors or rescheduling.\n\n");
            tmp3.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 0), 0, tmp3.length(), 0);
            tmp3.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp3.length(), 0);
            tmp3.setSpan(new RelativeSizeSpan(0.3f), tmp3.length()-1, tmp3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp4 = new SpannableString("Sensitive groups: \n");
            tmp4.setSpan(new StyleSpan(BOLD), 0, tmp4.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tmp4.setSpan(new RelativeSizeSpan(1.2f), 0, tmp4.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp5 = new SpannableString("Avoid prolonged or heavy exertion.\n\n");
            tmp5.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 0), 0, tmp5.length(), 0);
            tmp5.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp5.length(), 0);
            tmp5.setSpan(new RelativeSizeSpan(0.3f), tmp5.length()-1, tmp5.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp6 = new SpannableString("Consider moving activities indoors or rescheduling.\n\n");
            tmp6.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 0), 0, tmp6.length(), 0);
            tmp6.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp6.length(), 0);
            tmp6.setSpan(new RelativeSizeSpan(0.3f), tmp6.length()-1, tmp6.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            str = TextUtils.concat(tmp1, tmp2, tmp3, tmp4, tmp5, tmp6);
        }
        else if (pm25 < 301){
            SpannableString tmp1 = new SpannableString("Sensitive groups: \n");
            tmp1.setSpan(new StyleSpan(BOLD), 0, tmp1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tmp1.setSpan(new RelativeSizeSpan(1.2f), 0, tmp1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp2 = new SpannableString("Avoid all physical activity outdoors.\n\n");
            tmp2.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 0), 0, tmp2.length(), 0);
            tmp2.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp2.length(), 0);
            tmp2.setSpan(new RelativeSizeSpan(0.3f), tmp2.length()-1, tmp2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp3 = new SpannableString("Move activities indoors or reschedule to a time when air quality is better.\n\n");
            tmp3.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 0), 0, tmp3.length(), 0);
            tmp3.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp3.length(), 0);
            tmp3.setSpan(new RelativeSizeSpan(0.3f), tmp3.length()-1, tmp3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp4 = new SpannableString("Everyone else: \n");
            tmp4.setSpan(new StyleSpan(BOLD), 0, tmp4.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tmp4.setSpan(new RelativeSizeSpan(1.2f), 0, tmp4.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp5 = new SpannableString("Avoid prolonged or heavy exertion.\n\n");
            tmp5.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 0), 0, tmp5.length(), 0);
            tmp5.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp5.length(), 0);
            tmp5.setSpan(new RelativeSizeSpan(0.3f), tmp5.length()-1, tmp5.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp6 = new SpannableString("Consider moving activities indoors or rescheduling to a time when air quality is better.\n\n");
            tmp6.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 0), 0, tmp6.length(), 0);
            tmp6.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp6.length(), 0);
            tmp6.setSpan(new RelativeSizeSpan(0.3f), tmp6.length()-1, tmp6.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            str = TextUtils.concat(tmp1, tmp2, tmp3, tmp4, tmp5, tmp6);
        }
        else {
            SpannableString tmp1 = new SpannableString("Everyone:\n");
            tmp1.setSpan(new StyleSpan(BOLD), 0, tmp1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tmp1.setSpan(new RelativeSizeSpan(1.2f), 0, tmp1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp2 = new SpannableString("Avoid all physical activity outdoors.\n\n");
            tmp2.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 0), 0, tmp2.length(), 0);
            tmp2.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp2.length(), 0);
            tmp2.setSpan(new RelativeSizeSpan(0.3f), tmp2.length()-1, tmp2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp3 = new SpannableString("Sensitive groups:\n");
            tmp3.setSpan(new StyleSpan(BOLD), 0, tmp3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tmp3.setSpan(new RelativeSizeSpan(1.2f), 0, tmp3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp4 = new SpannableString("Remain indoors and keep activity levels low.\n\n");
            tmp4.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 30), 0, tmp4.length(), 0);
            tmp4.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp4.length(), 0);
            tmp4.setSpan(new RelativeSizeSpan(0.3f), tmp4.length()-1, tmp4.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp5 = new SpannableString("Follow tips for keeping particle levels low indoors.\n\n");
            tmp5.setSpan(new android.text.style.LeadingMarginSpan.Standard(30, 30), 0, tmp5.length(), 0);
            tmp5.setSpan(new BulletSpan(20, Color.BLACK), 0, tmp5.length(), 0);
            tmp5.setSpan(new RelativeSizeSpan(0.3f), tmp5.length()-1, tmp5.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp6 = new SpannableString("Eliminate tobacco smoke.\n\n");
            tmp6.setSpan(new android.text.style.LeadingMarginSpan.Standard(80, 80), 0, tmp6.length(), 0);
            tmp6.setSpan(new BulletSpan(20, Color.BLUE), 0, tmp6.length(), 0);
            tmp6.setSpan(new RelativeSizeSpan(0.3f), tmp6.length()-1, tmp6.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp7 = new SpannableString("Reduce your use of wood stoves and fireplaces. \n\n");
            tmp7.setSpan(new android.text.style.LeadingMarginSpan.Standard(80, 80), 0, tmp7.length(), 0);
            tmp7.setSpan(new BulletSpan(20, Color.BLUE), 0, tmp7.length(), 0);
            tmp7.setSpan(new RelativeSizeSpan(0.3f), tmp7.length()-1, tmp7.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp8 = new SpannableString("Use HEPA air filters and air cleaners designed to reduce particles.\n\n");
            tmp8.setSpan(new android.text.style.LeadingMarginSpan.Standard(80, 80), 0, tmp8.length(), 0);
            tmp8.setSpan(new BulletSpan(20, Color.BLUE), 0, tmp8.length(), 0);
            tmp8.setSpan(new RelativeSizeSpan(0.3f), tmp8.length()-1, tmp8.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString tmp9 = new SpannableString("Don't burn candles.\n");
            tmp9.setSpan(new android.text.style.LeadingMarginSpan.Standard(80, 80), 0, tmp9.length(), 0);
            tmp9.setSpan(new BulletSpan(20, Color.BLUE), 0, tmp9.length(), 0);

            str = TextUtils.concat(tmp1, tmp2, tmp3, tmp4, tmp5, tmp6, tmp7, tmp8, tmp9);
        }
        return str;
    }
}