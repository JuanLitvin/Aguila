using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace AguilaTest
{
    public partial class Form1 : Form
    {

        List<Widget> widgets = new List<Widget>();
        Mirror mirror;

        public Form1()
        {
            InitializeComponent();
        }

        private void btnAgregar_Click(object sender, EventArgs e)
        {
            Widget widget = new Widget(txtWidgetPath.Text);
            widgets.Add(widget);
            updateWidgets();
        }

        private void updateWidgets()
        {
            panelPreview.Controls.Clear();
            foreach(Widget w in widgets)
            {
                if (panelPreview.Controls.Count == 1)
                {
                    w.Left = 300;
                }
                panelPreview.Controls.Add(w);
            }
        }

        private void btnLaunch_Click(object sender, EventArgs e)
        {
            //if (mirror != null) mirror.Close();
            //mirror = new Mirror(widgets);
            //mirror.Show();
        }
    }
}
