using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Drawing;
using System.Net;
using facebook.Utility;

namespace facebook.Schema
{
    public partial class album
    {
        public DateTime created_date
        {
            get { return DateHelper.ConvertDoubleToDate(Convert.ToDouble(this.created)); }
        }
        public DateTime modified_date
        {
            get { return DateHelper.ConvertDoubleToDate(Convert.ToDouble(this.modified)); }
        }
    }
    public partial class photo_tag
    {
        public DateTime created_date
        {
            get { return DateHelper.ConvertDoubleToDate(Convert.ToDouble(this.created)); }
        }
    }
    public partial class photo
    {
        private Image _picture;
        private Image _pictureBig;
        private Image _pictureSmall;
        public Image picture
        {
            get
            {
                if (this.src == null)
                {
                    return Properties.Resources.missingPicture;
                }
                else if (_picture == null)
                {
                    WebClient webClient = new WebClient();
                    Byte[] pictureBytes = webClient.DownloadData(this.src);
                    _picture = ImageHelper.ConvertBytesToImage(pictureBytes);
                    return _picture;
                }
                else
                {
                    return _picture;
                }
            }
        }
        public Image picture_big
        {
            get
            {
                if (this.src_big == null)
                {
                    return Properties.Resources.missingPicture;
                }
                else if (_pictureBig == null)
                {
                    WebClient webClient = new WebClient();
                    Byte[] pictureBytes = webClient.DownloadData(this.src_big);
                    _pictureBig = ImageHelper.ConvertBytesToImage(pictureBytes);
                    return _pictureBig;
                }
                else
                {
                    return _pictureBig;
                }
            }
        }
        public Image picture_small
        {
            get
            {
                if (this.src_small == null)
                {
                    return Properties.Resources.missingPicture;
                }
                else if (_pictureSmall == null)
                {
                    WebClient webClient = new WebClient();
                    Byte[] pictureBytes = webClient.DownloadData(this.src_small);
                    _pictureSmall = ImageHelper.ConvertBytesToImage(pictureBytes);
                    return _pictureSmall;
                }
                else
                {
                    return _pictureSmall;
                }
            }
        }
        public DateTime created_date
        {
            get { return DateHelper.ConvertDoubleToDate(Convert.ToDouble(this.created)); }
        }
    }
    public partial class facebookevent
    {
        private Image _picture;
        private Image _pictureBig;
        private Image _pictureSmall;
        public Image picture
        {
            get
            {
                if (this.pic == null)
                {
                    return Properties.Resources.missingPicture;
                }
                else if (_picture == null)
                {
                    WebClient webClient = new WebClient();
                    Byte[] pictureBytes = webClient.DownloadData(this.pic);
                    _picture = ImageHelper.ConvertBytesToImage(pictureBytes);
                    return _picture;
                }
                else
                {
                    return _picture;
                }
            }
        }
        public Image picture_big
        {
            get
            {
                if (this.pic_big == null)
                {
                    return Properties.Resources.missingPicture;
                }
                else if (_pictureBig == null)
                {
                    WebClient webClient = new WebClient();
                    Byte[] pictureBytes = webClient.DownloadData(this.pic_big);
                    _pictureBig = ImageHelper.ConvertBytesToImage(pictureBytes);
                    return _pictureBig;
                }
                else
                {
                    return _pictureBig;
                }
            }
        }
        public Image picture_small
        {
            get
            {
                if (this.pic_small == null)
                {
                    return Properties.Resources.missingPicture;
                }
                else if (_pictureSmall == null)
                {
                    WebClient webClient = new WebClient();
                    Byte[] pictureBytes = webClient.DownloadData(this.pic_small);
                    _pictureSmall = ImageHelper.ConvertBytesToImage(pictureBytes);
                    return _pictureSmall;
                }
                else
                {
                    return _pictureSmall;
                }
            }
        }
        public DateTime start_date
        {
            get { return DateHelper.ConvertDoubleToDate(Convert.ToDouble(this.start_time)); }
            set { this.start_time = (int)DateHelper.ConvertDateToDouble(value); }
        }
        public DateTime end_date
        {
            get { return DateHelper.ConvertDoubleToDate(Convert.ToDouble(this.end_time)); }
            set { this.end_time = (int)DateHelper.ConvertDateToDouble(value); }
        }
        public DateTime update_date
        {
            get { return DateHelper.ConvertDoubleToDate(Convert.ToDouble(this.update_time)); }
        }
    }
    public partial class group
    {
        private Image _picture;
        private Image _pictureBig;
        private Image _pictureSmall;
        public Image picture
        {
            get
            {
                if (this.pic == null)
                {
                    return Properties.Resources.missingPicture;
                }
                else if (_picture == null)
                {
                    WebClient webClient = new WebClient();
                    Byte[] pictureBytes = webClient.DownloadData(this.pic);
                    _picture = ImageHelper.ConvertBytesToImage(pictureBytes);
                    return _picture;
                }
                else
                {
                    return _picture;
                }
            }
        }
        public Image picture_big
        {
            get
            {
                if (this.pic_big == null)
                {
                    return Properties.Resources.missingPicture;
                }
                else if (_pictureBig == null)
                {
                    WebClient webClient = new WebClient();
                    Byte[] pictureBytes = webClient.DownloadData(this.pic_big);
                    _pictureBig = ImageHelper.ConvertBytesToImage(pictureBytes);
                    return _pictureBig;
                }
                else
                {
                    return _pictureBig;
                }
            }
        }
        public Image picture_small
        {
            get
            {
                if (this.pic_small == null)
                {
                    return Properties.Resources.missingPicture;
                }
                else if (_pictureSmall == null)
                {
                    WebClient webClient = new WebClient();
                    Byte[] pictureBytes = webClient.DownloadData(this.pic_small);
                    _pictureSmall = ImageHelper.ConvertBytesToImage(pictureBytes);
                    return _pictureSmall;
                }
                else
                {
                    return _pictureSmall;
                }
            }
        }
        public DateTime update_date
        {
            get { return DateHelper.ConvertDoubleToDate(Convert.ToDouble(this.update_time)); }
        }
    }
    public partial class user
    {
        private Image _picture;
        private Image _pictureBig;
        private Image _pictureSmall;
        private Image _pictureSquare;
        public Image picture
        {
            get
            {
                if (this.pic == null)
                {
                    return Properties.Resources.missingPicture;
                }
                else if (_picture == null)
                {
                    WebClient webClient = new WebClient();
                    Byte[] pictureBytes = webClient.DownloadData(this.pic);
                    _picture = ImageHelper.ConvertBytesToImage(pictureBytes);
                    return _picture;
                }
                else
                {
                    return _picture;
                }
            }
        }
        public Image picture_big
        {
            get
            {
                if (this.pic_big == null)
                {
                    return Properties.Resources.missingPicture;
                }
                else if (_pictureBig == null)
                {
                    WebClient webClient = new WebClient();
                    Byte[] pictureBytes = webClient.DownloadData(this.pic_big);
                    _pictureBig = ImageHelper.ConvertBytesToImage(pictureBytes);
                    return _pictureBig;
                }
                else
                {
                    return _pictureBig;
                }
            }
        }
        public Image picture_small
        {
            get
            {
                if (this.pic_small == null)
                {
                    return Properties.Resources.missingPicture;
                }
                else if (_pictureSmall == null)
                {
                    WebClient webClient = new WebClient();
                    Byte[] pictureBytes = webClient.DownloadData(this.pic_small);
                    _pictureSmall = ImageHelper.ConvertBytesToImage(pictureBytes);
                    return _pictureSmall;
                }
                else
                {
                    return _pictureSmall;
                }
            }
        }
        public Image picture_square
        {
            get
            {
                if (this.pic_square == null)
                {
                    return Properties.Resources.missingPicture;
                }
                else if (_pictureSquare == null)
                {
                    WebClient webClient = new WebClient();
                    Byte[] pictureBytes = webClient.DownloadData(this.pic_square);
                    _pictureSquare = ImageHelper.ConvertBytesToImage(pictureBytes);
                    return _pictureSquare;
                }
                else
                {
                    return _pictureSquare;
                }
            }
        }

        public DateTime birthday_date
        {
            get
            {
                //If we have a full date, it will come back as a double
                double dblBirthDay;
                if (Double.TryParse(this.birthday, out dblBirthDay))
                    return DateHelper.ConvertDoubleToDate(dblBirthDay);
                
                //If the user just has their birthday without a year, append 1901 to it and try to parse it.
                try
                {
                    var tempBirthday = this.birthday;
                    tempBirthday = tempBirthday + " 1901";
                    return DateTime.Parse(tempBirthday);
                    
                }
                catch(Exception e)
                {
                    //If we couldn't get any date, return 
                    return new DateTime(1900,1,1);
                }


            }

        }
        

    }
    public class feed_image
    {
        /// <summary>
        /// The URL of an image to be displayed in the News Feed story.
        /// </summary>
        public string image_url{ get; set; }

        /// <summary>
        /// The URL destination after a click on the image referenced by ImageURL.
        /// </summary>
        public string image_link_url { get; set; }

        /// <summary>
        /// constructor
        /// </summary>
        public feed_image(string image_url, string image_link_url)
        {
            this.image_url = image_url;
            this.image_link_url = image_link_url;
        }
    }
}
