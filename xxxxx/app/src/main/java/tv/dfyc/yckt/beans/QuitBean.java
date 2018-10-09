package tv.dfyc.yckt.beans;

import java.util.List;

/**
 * Created by android on 2018/3/21.
 */

public class QuitBean {
    private int SeqNo;
    private String Result;
    private String Message;
    private QuitData data;

    public int getSeqNo() {
        return SeqNo;
    }

    public void setSeqNo(int seqNo) {
        SeqNo = seqNo;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public QuitData getData() {
        return data;
    }

    public void setData(QuitData data) {
        this.data = data;
    }

    /* 退出信息 */
    public class QuitData {
        private String image_url;
        private String new_image_url;
        private List<QuitItem> tuijianwei;

        public String getNew_image_url() {
            return new_image_url;
        }

        public void setNew_image_url(String new_image_url) {
            this.new_image_url = new_image_url;
        }

        public List<QuitItem> getTuijianwei() {
            return tuijianwei;
        }

        public void setTuijianwei(List<QuitItem> tuijianwei) {
            this.tuijianwei = tuijianwei;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public class QuitItem {
            private String tuijianwei_category;
            private String tuijianwei_name;
            private String tuijianwei_image;
            private int tuijianwei_type;
            private String tuijianwei_id;

            public String getTuijianwei_category() {
                return tuijianwei_category;
            }

            public void setTuijianwei_category(String tuijianwei_category) {
                this.tuijianwei_category = tuijianwei_category;
            }

            public String getTuijianwei_name() {
                return tuijianwei_name;
            }

            public void setTuijianwei_name(String tuijianwei_name) {
                this.tuijianwei_name = tuijianwei_name;
            }

            public String getTuijianwei_image() {
                return tuijianwei_image;
            }

            public void setTuijianwei_image(String tuijianwei_image) {
                this.tuijianwei_image = tuijianwei_image;
            }

            public int getTuijianwei_type() {
                return tuijianwei_type;
            }

            public void setTuijianwei_type(int tuijianwei_type) {
                this.tuijianwei_type = tuijianwei_type;
            }

            public String getTuijianwei_id() {
                return tuijianwei_id;
            }

            public void setTuijianwei_id(String tuijianwei_id) {
                this.tuijianwei_id = tuijianwei_id;
            }
        }
    }
}
