using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using Microsoft.Kinect;
using Microsoft.Kinect.Face;
using System.Drawing;
using System.Windows.Media.Imaging;
using System.Runtime.InteropServices;


namespace Kinect_1
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {

        KinectSensor sensor;
        MultiSourceFrameReader reader;

        public MainWindow()
        {
            sensor = KinectSensor.GetDefault();

            if (sensor != null)
            {
                sensor.Open();

                reader = sensor.OpenMultiSourceFrameReader(FrameSourceTypes.Body | FrameSourceTypes.Color | FrameSourceTypes.Infrared);
                reader.MultiSourceFrameArrived += Reader_MultiSourceFrameArrived;
            } else
            {
                MessageBox.Show("Connect a Kinect and restart");
            }

            InitializeComponent();
        }

        private void Reader_MultiSourceFrameArrived(object sender, MultiSourceFrameArrivedEventArgs e)
        {
            var reference = e.FrameReference.AcquireFrame();

            using (ColorFrame frame = reference.ColorFrameReference.AcquireFrame())
            {
                if (frame != null)
                {
                    camera.Source = ToBitmap(frame);
                }
            }

            using (InfraredFrame frame = reference.InfraredFrameReference.AcquireFrame())
            {
                if (frame != null)
                {
                    //camera.Source = ToBitmap(frame);
                }
            }

            using (BodyFrame frame = reference.BodyFrameReference.AcquireFrame())
            {
                if (frame != null)
                {
                    Body[] _bodies = new Body[frame.BodyFrameSource.BodyCount];

                    frame.GetAndRefreshBodyData(_bodies);

                    foreach (var body in _bodies)
                    {
                        canvas.Children.Clear();

                        if (body != null)
                        {
                            lbl1.Content = body.Joints[JointType.Head].Position.X + " " + body.Joints[JointType.Head].Position.Y;

                            Joint joint = body.Joints[JointType.HandLeft];
                            Ellipse ellipse = new Ellipse();
                            ellipse.Height = 20;
                            ellipse.Width = 20;
                            double CWidth = canvas.Width;
                            double CHeight = canvas.Height;

                            //ColorSpacePoint point = sensor.CoordinateMapper.MapCameraPointToColorSpace(joint.Position);
                            //CWidth / 2 - ellipse.Width / 2 + joint.Position.X * CWidth / 2, CHeight / 2 + joint.Position.Y * (-CHeight) / 2

                            SetCursorPos((int) (CWidth / 2 - ellipse.Width / 2 + joint.Position.X * CWidth / 2), (int) (CHeight / 2 + joint.Position.Y * (-CHeight) / 2));

                            if (true)
                            //if (point.X != Double.NegativeInfinity && point.Y != Double.NegativeInfinity)
                            {
                                ellipse.Margin = new Thickness(CWidth / 2 - ellipse.Width / 2 + joint.Position.X * CWidth / 2, CHeight / 2 + joint.Position.Y * (-CHeight) / 2, 0, 0);
                                //ellipse.Margin = new Thickness(point.X, point.Y, 0, 0);
                                ellipse.Fill = System.Windows.Media.Brushes.Red;
                                canvas.Children.Add(ellipse);
                            }

                            /*foreach (Joint joint in body.Joints.Values)
                            {
                                //DrawPoint(canvas, joint);
                                Ellipse ellipse = new Ellipse();
                                ellipse.Height = 20;
                                ellipse.Width = 20;
                                //double CWidth = canvas.Width;
                                //double CHeight = canvas.Height;

                                ColorSpacePoint point = sensor.CoordinateMapper.MapCameraPointToColorSpace(joint.Position);
                                //DrawPoint(point);

                                if (point.X != Double.NegativeInfinity && point.Y != Double.NegativeInfinity)
                                {
                                    //ellipse.Margin = new Thickness(CWidth / 2 - ellipse.Width / 2 + joint.Position.X * CWidth / 2, CHeight / 2 + joint.Position.Y * (-CHeight) / 2, 0, 0);
                                    ellipse.Margin = new Thickness(point.X, point.Y, 0, 0);
                                    ellipse.Fill = System.Windows.Media.Brushes.Red;
                                    canvas.Children.Add(ellipse);
                                }
                            }*/
                        }
                    }
                }
            }
        }

        [DllImport("User32.dll")]
        private static extern bool SetCursorPos(int X, int Y);

        public void DrawPoint(ColorSpacePoint point)
        {
            // Create an ellipse.
            Ellipse ellipse = new Ellipse
            {
                Width = 20,
                Height = 20,
                Fill = System.Windows.Media.Brushes.Red
            };

            // Position the ellipse according to the point's coordinates.
            if (point.X != Double.NegativeInfinity && point.Y != Double.NegativeInfinity)
            {
                Canvas.SetLeft(ellipse, point.X - ellipse.Width / 2);
                Canvas.SetTop(ellipse, point.Y - ellipse.Height / 2);
            }

            // Add the ellipse to the canvas.
            canvas.Children.Add(ellipse);
        }

        private ImageSource ToBitmap(ColorFrame frame)
        {
            int width = frame.FrameDescription.Width;
            int height = frame.FrameDescription.Height;

            byte[] pixels = new byte[width * height * ((PixelFormats.Bgr32.BitsPerPixel + 7) / 8)];

            if (frame.RawColorImageFormat == ColorImageFormat.Bgra)
            {
                frame.CopyRawFrameDataToArray(pixels);
            }
            else
            {
                frame.CopyConvertedFrameDataToArray(pixels, ColorImageFormat.Bgra);
            }

            int stride = width * PixelFormats.Bgr32.BitsPerPixel / 8;

            return BitmapSource.Create(width, height, 96, 96, PixelFormats.Bgr32, null, pixels, stride);
        }

        private ImageSource ToBitmap(InfraredFrame frame)
        {
            int width = frame.FrameDescription.Width;
            int height = frame.FrameDescription.Height;

            ushort[] infraredData = new ushort[width * height];
            byte[] pixelData = new byte[width * height * (PixelFormats.Bgr32.BitsPerPixel + 7) / 8];

            frame.CopyFrameDataToArray(infraredData);

            int colorIndex = 0;
            for (int infraredIndex = 0; infraredIndex < infraredData.Length; ++infraredIndex)
            {
                ushort ir = infraredData[infraredIndex];
                byte intensity = (byte)(ir >> 8);

                pixelData[colorIndex++] = intensity; // Blue
                pixelData[colorIndex++] = intensity; // Green   
                pixelData[colorIndex++] = intensity; // Red

                ++colorIndex;
            }

            int stride = width * PixelFormats.Bgr32.BitsPerPixel / 8;

            return BitmapSource.Create(width, height, 96, 96, PixelFormats.Bgr32, null, pixelData, stride);
        }
    }
}
