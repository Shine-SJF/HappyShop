package ci553.happyshop.utility;

import java.util.ArrayList;
import java.util.List;
/**
 * UIStyle is a centralized Java final class that holds all JavaFX UI-related style and size constants
 * used across all client views in the system.
 *
 * These values are grouped here rather than being hardcoded throughout the codebase:
 * - improves maintainability, ensures style consistency,
 * - avoids hardcoded values scattered across the codebase.
 *
 * Example usages:
 * - UIStyle.HistoryWinHeight for setting the height of the order history window
 * - UIStyle.labelStyle for applying consistent styling to labels
 *
 * Design rationale:
 * - Declared as a final class: prevents inheritance and misuse
 * - Private constructor: prevents instantiation (this is a static-only utility class)
 * - Holds only static constants: ensures minimal memory usage and clean syntax
 *
 *  Why a Record is NOT appropriate:
 * - Records are intended for immutable instance data (e.g., DTOs), not static constants
 * - This class has no record components — everything is static
 * - We're using this as a utility container, not a data model
 *
 *  Reminder:
 *  Just because a class has no behavior and only data does NOT mean it should be a record.
 *  If all members are static constants, use a final utility class like this one.
 */

public final class UIStyle {

    //private constructor prevents instantiation
    private UIStyle() {
        throw new UnsupportedOperationException("UIStyle is a utility class");
    }

    public static boolean isDarkMode = false;

    public static final int customerWinWidth = 600;

    public static final int customerWinWidth = 610;

    public static final int customerWinHeight = 300;
    public static final int removeProNotifierWinWidth = customerWinWidth / 2 + 160;
    public static final int removeProNotifierWinHeight = 230;

    public static final int pickerWinWidth = 310;
    public static final int pickerWinHeight = 300;

    public static final int trackerWinWidth = 210;
    public static final int trackerWinHeight = 300;

    public static final int warehouseWinWidth = 630;
    public static final int warehouseWinHeight = 300;
    public static final int AlertSimWinWidth = 300;
    public static final int AlertSimWinHeight = 170;
    public static final int HistoryWinWidth = 310;
    public static final int HistoryWinHeight = 300;

    public static final int EmergencyExitWinWidth = 200;

    public static final int EmergencyExitWinHeight = 250;

    public static String rootStyle;
    public static String rootStyleHistory;
    public static String labelTitleStyle;
    public static String labelStyle;
    public static String labelShutdown;
    public static String labelMulLineStyle;
    public static String textFiledStyle;
    public static String historyTextFiledStyle;
    public static String textAreaStyle;
    public static String buttonStyle;
    public static String comboBoxStyle;
    public static String comboBoxPopupStyle;
    public static String listViewStyle;
    public static String receiptPageStyle;
    public static String greenFillBtnStyle;
    public static String redFillBtnStyle;
    public static String greenFillBtnStyle2;
    public static String redFillBtnStyle2;
    public static String blueFillBtnStyle;
    public static String grayFillBtnStyle;
    public static String buttonFillBtnStyle;
    public static String exitBtnStyle;
    public static String manageStockChildStyle;
    public static String manageStockChildStyle1;
    public static String cardStyle;
    public static String alertTitleLabelStyle;
    public static String alertContentTextAreaStyle;
    public static String alertContentUserActionStyle;
    public static String alertBtnStyle;
    public static String lineStyle;
    public static String detailAreaStyle;

    // Corner Radius Variable
    private static final String ROUND_CORNER = " -fx-background-radius: 25; -fx-border-radius: 25; ";
    private static final String BTN_PADDING = " -fx-padding: 8 22 8 22; ";
    // Font Style Variable
    private static final String FONT_MAIN = "-fx-font-family: 'Muli Black'; ";

    // A list of actions to run when the theme changes
    private static final List<Runnable> themeListeners = new ArrayList<>();
    public static void addThemeListener(Runnable r) {
        if (!themeListeners.contains(r)) {
            themeListeners.add(r);
        }
    }

    static {
        setDarkMode(false); // Default to Light
    }

    public static void setDarkMode(boolean dark) {
        isDarkMode = dark;
        if (dark) {
            //DARK MODE
            rootStyle = "-fx-background-color: #121212; -fx-padding: 8px;";
            rootStyleHistory = "-fx-background-color: #1A1A1A";
            labelTitleStyle = FONT_MAIN + "-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #E2B327;";
            labelStyle = FONT_MAIN + "-fx-text-fill: #E2B327; -fx-font-weight: bold; -fx-font-size: 14px;";
            labelShutdown = "-fx-text-fill: #F44236; " + "-fx-font-family: 'Muli Black'; " + "-fx-font-weight: bold; " + "-fx-font-size: 20px; " + "-fx-letter-spacing: 2px;";
            labelMulLineStyle = FONT_MAIN + "-fx-font-size: 12px; -fx-text-fill: #FFFFFF;";
            textFiledStyle = "-fx-pref-height: 30;" + "-fx-background-color: #1E1E1E; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 1;" + ROUND_CORNER;
            historyTextFiledStyle = "-fx-font-family: 'Muli Black'; " + "-fx-font-size: 13px; " + "-fx-control-inner-background: #1E1E1E; " + "-fx-background-color: #1E1E1E ; " + "-fx-background-insets: 0, 2; " + "-fx-background-radius: 0; " + "-fx-border-radius:0;" + "-fx-border-color: white; " + "-fx-border-width: 0.5; " + "-fx-padding: 0;";
            textAreaStyle = "-fx-font-size: 14px; -fx-background-color: #2b2b2b; -fx-text-fill: white; -fx-border-color: #888888;";
            buttonStyle = FONT_MAIN + "-fx-pref-height: 30; -fx-background-color: #E2B327; -fx-text-fill: black; -fx-font-weight: bold;" + ROUND_CORNER + BTN_PADDING;
            comboBoxStyle = "-fx-pref-height: 30; -fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #FFFFFF; -fx-prompt-text-fill: #FFFFFF; -fx-faint-focus-color: transparent; -fx-focus-color: transparent; -fx-mark-color: black; -fx-background-color:#E2B327; -fx-border-color:#E2B327; -fx-border-width: 1;" + ROUND_CORNER;
            comboBoxPopupStyle =  "data:text/css," + (".combo-box-popup { -fx-background-color: transparent; }" + ".combo-box-popup > .list-view {" + "-fx-background-color: #2b2b2b;" + "-fx-background-radius: 15;" + "-fx-border-radius: 15;" + "-fx-border-color: #444444;" + "-fx-border-width: 1;" + "}" +".combo-box-popup > .list-view > .virtual-flow > .clipped-container > .sheet > .list-cell {" +"-fx-text-fill: white;" +"-fx-background-color: transparent;" +"}" +".combo-box-popup > .list-view > .virtual-flow > .clipped-container > .sheet > .list-cell:hover {" +" -fx-background-color: #3d3d3d;" +"}").replace(" ", "%20");
            listViewStyle = "-fx-font-family: 'Muli Black'; " + "-fx-font-size: 13px; " + "-fx-control-inner-background: #1E1E1E; " + "-fx-background-color: #1E1E1E ; " + "-fx-background-insets: 0, 2; " + "-fx-background-radius: 0; " + "-fx-border-radius:0;" + "-fx-border-color: white; " + "-fx-border-width: 0.5; " + "-fx-padding: 0;";
            receiptPageStyle = "-fx-background-color: #2D2D2D; -fx-border-color: white; -fx-border-width: 2; -fx-background-radius: 15; -fx-border-radius: 15; -fx-padding: 15;";
            greenFillBtnStyle = FONT_MAIN + "-fx-pref-height: 30;" + " -fx-padding: 8 15 8 15; " + "-fx-font-weight: bold;" + "-fx-background-color: #22943C; -fx-text-fill: white;" + ROUND_CORNER;
            greenFillBtnStyle2 = FONT_MAIN + "-fx-pref-height: 30;" + "-fx-padding: 8 15 8 15;" + "-fx-background-color: #22943C; -fx-font-weight: bold; -fx-font-size: 18px;" + "-fx-text-fill: #FFFFFF;" + ROUND_CORNER;
            grayFillBtnStyle = FONT_MAIN + "-fx-pref-height: 30;" + "-fx-padding: 0 60 0 60; " + "-fx-background-color: #F44236; -fx-text-fill: white; -fx-font-weight: bold;" + ROUND_CORNER + BTN_PADDING;
            blueFillBtnStyle = FONT_MAIN + "-fx-pref-height: 30;" + "-fx-background-color: #136ECF; -fx-text-fill: white; -fx-font-weight: bold;" + ROUND_CORNER + BTN_PADDING;
            redFillBtnStyle2 = FONT_MAIN + "-fx-pref-height: 30;" + "-fx-padding: 8 15 8 15;" + "-fx-background-color: #D12F26; -fx-font-weight: bold; -fx-font-size: 18px;" + "-fx-text-fill: #FFFFFF;" + ROUND_CORNER;
            redFillBtnStyle = FONT_MAIN + "-fx-pref-height: 30;" + "-fx-background-color: #D12F26; -fx-text-fill: white; -fx-font-weight: bold;" + ROUND_CORNER + BTN_PADDING;
            buttonFillBtnStyle = FONT_MAIN + "-fx-background-color: #E2B327;" + "-fx-border-color: #E2B327;" + "-fx-background-radius: 50;" + "-fx-border-radius: 55;" + "-fx-text-fill: #121212;" + "-fx-font-weight: bold;";
            exitBtnStyle = "-fx-background-color: #1E1E1E; " + "-fx-background-radius: 80; " + "-fx-padding: 20; -fx-border-radius: 80; -fx-border-color: #F44236; -fx-border-width: 4 ";
            manageStockChildStyle = "-fx-background-color:  #1E1E1E;  -fx-background-radius: 15; -fx-padding: 10;";
            manageStockChildStyle1 = "-fx-background-color: #332B00; -fx-border-color: #554400; -fx-padding: 5px;";
            cardStyle = "-fx-background-color: #2D2D2D; -fx-background-radius: 15; -fx-padding: 10;";
            alertTitleLabelStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FF5252; -fx-background-color: #2D0000;";
            alertContentTextAreaStyle = "-fx-font-size: 14px; -fx-control-inner-background: #252525; -fx-text-fill: #80CBC4; -fx-border-color: #444;";
            alertContentUserActionStyle = "-fx-font-size: 14px; -fx-text-fill: #69F0AE;";
            alertBtnStyle = "-fx-background-color: #E2B327; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 20;";

        } else {
            //LIGHT MODE (Originals)
            rootStyle = "-fx-background-color: #E2B327; -fx-padding: 8px;";
            rootStyleHistory = "-fx-background-color: #E2B327";
            labelTitleStyle = FONT_MAIN + "-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #121212;";
            labelStyle = FONT_MAIN + "-fx-text-fill: #121212; -fx-font-weight: bold; -fx-font-size: 14px;";
            labelShutdown = "-fx-text-fill: #D12F26; " + "-fx-font-family: 'Muli Black'; " + "-fx-font-weight: bold; " + "-fx-font-size: 20px; " + "-fx-letter-spacing: 2px;";
            labelMulLineStyle = FONT_MAIN + "-fx-font-size: 12px; -fx-text-fill: #333333;";
            textFiledStyle = "-fx-pref-height: 30;" + "-fx-background-color: white; -fx-text-fill: #121212; -fx-border-color: #121212; -fx-border-width: 1;" + ROUND_CORNER;
            historyTextFiledStyle = "-fx-pref-height: 30;" + "-fx-background-color: white; -fx-text-fill: #121212; -fx-border-color: #121212; -fx-border-width: 1;";
            textAreaStyle = "-fx-font-size: 14px; -fx-background-color: white; -fx-text-fill: black; -fx-border-color: #121212;";
            buttonStyle = FONT_MAIN + "-fx-pref-height: 30;" + "-fx-background-color: #121212; -fx-text-fill: #E2B327; -fx-font-weight: bold; " + ROUND_CORNER + BTN_PADDING;
            comboBoxStyle = "-fx-pref-height: 30; -fx-font-weight: bold; -fx-text-fill: #121212; -fx-font-size: 14px; -fx-background-color: #FFFFFF;" + ROUND_CORNER;
            comboBoxPopupStyle =  "data:text/css," + (".combo-box-popup { -fx-background-color: transparent; }" + ".combo-box-popup > .list-view {" + "-fx-background-color: white;" + "-fx-background-radius: 15;" + "-fx-border-radius: 15;" + "-fx-border-color: #cccccc;" + "-fx-border-width: 1;" +  "}").replace(" ", "%20");
            listViewStyle = "-fx-font-family: 'Muli Black'; " + "-fx-font-size: 13px; " + "-fx-control-inner-background: white; " + "-fx-background-color: #121212, white; " + "-fx-background-insets: 0, 2; " + "-fx-background-radius: 0; " + "-fx-border-radius:0;" + "-fx-border-color: #121212; " + "-fx-border-width: 0.5; " + "-fx-padding: 0;";
            receiptPageStyle = "-fx-background-color: white; -fx-border-color: #121212; -fx-border-width: 2; -fx-background-radius: 15; -fx-border-radius: 15; -fx-padding: 15;";
            greenFillBtnStyle = FONT_MAIN + "-fx-pref-height: 30;" + " -fx-padding: 8 15 8 15; " + "-fx-font-weight: bold;" + "-fx-background-color: #28a745; -fx-text-fill: white;" + ROUND_CORNER;
            greenFillBtnStyle2 = FONT_MAIN + "-fx-pref-height: 30;" + "-fx-padding: 8 15 8 15;" + "-fx-background-color: #28a745; -fx-font-weight: bold; -fx-font-size: 18px;" + "-fx-text-fill: #121212;" + ROUND_CORNER;
            grayFillBtnStyle = FONT_MAIN + "-fx-pref-height: 30;" + "-fx-padding: 0 60 0 60; " + "-fx-background-color: #F44236; -fx-text-fill: white; -fx-font-weight: bold;" + ROUND_CORNER + BTN_PADDING;
            blueFillBtnStyle = FONT_MAIN + "-fx-pref-height: 30;" + "-fx-background-color: #007BFF; -fx-text-fill: white; -fx-font-weight: bold;" + ROUND_CORNER + BTN_PADDING;
            redFillBtnStyle = FONT_MAIN + "-fx-pref-height: 30;" + "-fx-background-color: #F44236; -fx-text-fill: white; -fx-font-weight: bold;" + ROUND_CORNER + BTN_PADDING;
            redFillBtnStyle2 = FONT_MAIN + "-fx-pref-height: 30;" + "-fx-padding: 8 15 8 15;" + "-fx-background-color: #F44236; -fx-font-weight: bold; -fx-font-size: 18px;" + "-fx-text-fill: #121212;" + ROUND_CORNER;
            buttonFillBtnStyle = FONT_MAIN + "-fx-background-color: #121212;" + "-fx-border-color: #121212;" + "-fx-background-radius: 50;" + "-fx-border-radius: 55;" + "-fx-text-fill: #E2B327;" + "-fx-font-weight: bold;";
            exitBtnStyle = "-fx-background-color: #F44236; " + "-fx-background-radius: 80; " + "-fx-padding: 20; -fx-border-radius: 80; -fx-border-color: #D12F26; -fx-border-width: 4";
            manageStockChildStyle = "-fx-background-color: white;  -fx-background-radius: 15; -fx-padding: 10;";
            manageStockChildStyle1 = "-fx-background-color: white; -fx-padding: 5px; -fx-border-radius: 15;";
            cardStyle = "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 10;";
            alertTitleLabelStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #B48A00; -fx-background-color: #FFF9E6;";
            alertContentTextAreaStyle = "-fx-font-size: 14px; -fx-control-inner-background: #FFFFFF; -fx-text-fill: #333333; -fx-border-color: #E2B327;";
            alertContentUserActionStyle = "-fx-font-size: 14px; -fx-text-fill: #E2B327; -fx-font-weight: bold;";
            alertBtnStyle = "-fx-background-color: #E2B327; -fx-text-fill: white; -fx-background-radius: 8;";
            lineStyle = "-fx-background-color: #121212; -fx-min-width: 2px; -fx-max-width: 2px;";
            detailAreaStyle = "-fx-font-family: 'Muli Black'; " + "-fx-font-size: 14px; " + "-fx-text-fill: #121212; " + "-fx-control-inner-background: #FFFFFF; " + "-fx-border-color: #121212; " + "-fx-border-width: 2;";
        }

        for (Runnable listener : themeListeners) {
            listener.run();
        }
    }
=======
    public static final int EmergencyExitWinHeight = 300;

    public static final String labelTitleStyle = "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; -fx-text-fill: purple;";

    public static final String labelStyle = "-fx-font-weight: bold; " +
            "-fx-font-size: 14px; " +
            "-fx-text-fill: black; " +
            "-fx-background-color: lightblue;";

    public static final String labelLowStockStyle =
            "-fx-font-size: 12px; -fx-text-fill: red;";

    public static final String comboBoxStyle = "-fx-font-weight: bold; " +
            "-fx-font-size: 14px;";

    public static final String buttonStyle = "-fx-font-size: 15";

    public static final String rootStyle = "-fx-padding: 8px; " +
            "-fx-background-color: lightgreen";

    public static final String rootStyleBlue = "-fx-padding: 8px; " +
            "-fx-background-color: lightblue";

    public static final String rootStyleGray = "-fx-padding: 8px; " +
            "-fx-background-color: lightgray";

    public static final String rootStyleWarehouse = "-fx-padding: 8px; " +
            "-fx-background-color: lightpink";

    public static final String rootStyleYellow = "-fx-padding: 8px; " +
            "-fx-background-color: lightyellow";

    public static final String rootVipCustomerStyle = "-fx-padding: 8px; " +
            "-fx-background-color: burlywood";

    public static final String spinnerArrowStyle = "-fx-font-size: 12px; -fx-padding: 0;";

    public static final String textFiledStyle = "-fx-font-size: 16";
    public static final String smallTextFiledStyle = "-fx-font-size: 14";
    public static final String tinyTextFiledStyle = "-fx-font-size: 12";

    public static final String labelMulLineStyle = "-fx-font-size: 16px; " +
            "-fx-background-color: lightpink";

    public static final String labelPriceStyle = "-fx-font-size: 16px; " +
            "-fx-background-color: lightyellow";

    public static final String listViewStyle = "-fx-border-color: #ccc; " +
            "-fx-border-width: 1px; -fx-background-color: white; -fx-font-size: 14px;";

    public static final String manageStockChildStyle = "-fx-background-color: lightgrey; " +
            "-fx-border-color: lightgrey; " +
            "-fx-border-width: 1px; " +
            "-fx-padding: 5px;";

    public static final String manageStockChildStyle1 = "-fx-background-color: lightyellow; " +
            "-fx-border-color: lightyellow; " +
            "-fx-border-width: 1px; " +
            "-fx-padding: 5px;";

    public static final String greenFillBtnStyle = "-fx-background-color: green; " +
            "-fx-text-fill: white; -fx-font-size: 14px;";
    public static final String redFillBtnStyle = "-fx-background-color: red; " +
            "-fx-text-fill: white; -fx-font-size: 14px; ";

    public static final String searchBtnStyle = "-fx-background-color: purple; " +
            "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;";

    public static final String grayFillBtnStyle = "-fx-background-color: gray; " +
            "-fx-text-fill: white; -fx-font-size: 14px; ";

    public static final String blueFillBtnStyle = "-fx-background-color: blue; " +
            "-fx-text-fill: white; -fx-font-size: 14px;";
    public static final String alertBtnStyle = "-fx-background-color: green; " +
            "-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;";

    public static final String alertTitleLabelStyle = "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: red; " + "-fx-background-color: lightblue;";

    public static final String alertContentTextAreaStyle = "-fx-font-size: 14px;" +
            "-fx-font-weight: normal;-fx-control-inner-background: lightyellow; -fx-text-fill: darkblue;";

    public static final String alertContentUserActionStyle = "-fx-font-size: 14px;" +
            "-fx-font-weight: normal; -fx-text-fill: green;";

    public static final String tooltipStyle = "-fx-background-color: lightyellow; -fx-text-fill: red;";

}
