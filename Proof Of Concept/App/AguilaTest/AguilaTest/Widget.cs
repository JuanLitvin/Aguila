using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.IO;

namespace AguilaTest
{
    public class Widget : WebBrowser
    {
        private string WidgetName;
        private Dictionary<string, string> metas = new Dictionary<string, string>();
        private string Path;

        public Widget(string filePath)
        {
            Path = filePath;

            loadMetas(@filePath);

            setHeight(getIntMeta("MODULE_HEIGHT"));
            setWidth(getIntMeta("MODULE_WIDTH"));
            setName(getStringMeta("MODULE_NAME"));

            defaultConfig();

            this.Navigate(filePath);
            //this.Document.Encoding = "utf-8";
        }

        private void defaultConfig()
        {
            this.ScrollBarsEnabled = false;
        }

        private void loadMetas(string filePath)
        {
            Regex metaTag = new Regex("<meta name=\"(.+?)\" content=\"(.+?)\">");

            string fileInput = File.ReadAllText(@filePath);

            foreach (Match m in metaTag.Matches(fileInput))
            {
                metas.Add(m.Groups[1].Value, m.Groups[2].Value);
            }
        }
           
        private int getIntMeta(string name)
        {
            int res;
            Int32.TryParse(metas[name], out res);
            return res * 100;
        }

        private string getStringMeta(string name)
        {
            return metas[name];
        }

        public void setHeight(int height)
        {
            if (height != 100 && height != 200 && height != 300 && height != 400 && height != 500 && height != 600 && height != 700) throw new Exception("Invalid Height");

            this.Height = height;
        }

        public void setWidth(int width)
        {
            if (width != 100 && width != 200 && width != 300 && width != 400 && width != 500 && width != 600) throw new Exception("Invalid Height");

            this.Width = width;
        }

        public void setName(string name)
        {
            WidgetName = name;
        }

        public class WidgetHeight
        {
            private WidgetHeight() { }
            public static int SIZE_1 = 100;
            public static int SIZE_2 = 200;
            public static int SIZE_3 = 300;
            public static int SIZE_4 = 400;
            public static int SIZE_5 = 500;
            public static int SIZE_6 = 600;
            public static int SIZE_7 = 700;
        }

        public class WidgetWidth
        {
            private WidgetWidth() { }
            public static int SIZE_1 = 100;
            public static int SIZE_2 = 200;
            public static int SIZE_3 = 300;
            public static int SIZE_4 = 400;
            public static int SIZE_5 = 500;
            public static int SIZE_6 = 600;
        }

    }
}
