package tv.dfyc.yckt.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2017-8-2.
 */

public class JsonVideoDetailData implements Serializable {

    private String SeqNo;
    private int Result;
    private String Message;
    private detailData data;

    @Override
    public String toString() {
        return "JsonVideoDetailData{" +
                "SeqNo='" + SeqNo + '\'' +
                ", Result=" + Result +
                ", Message='" + Message + '\'' +
                ", data=" + data +
                '}';
    }

    public String getSeqNo() {
        return SeqNo;
    }

    public void setSeqNo(String seqNo) {
        SeqNo = seqNo;
    }

    public int getResult() {
        return Result;
    }

    public void setResult(int result) {
        Result = result;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public detailData getData() {
        return data;
    }

    public void setData(detailData data) {
        this.data = data;
    }


    public class detailData implements Serializable {
        private int library_id;
        private String title;
        private String thumb_app_h;
        private String tags;
        private int pv;
        private String main_speaker;
        private String description;
        private String url;
        private int is_free;//0 免费 1 收费
        private int collect;//是否收藏 0 未收藏 1 收藏
        private int historyid;
        private int isOrder;//0 未订购 1 已订购
        private int library_detail_id;
        private int last_play_time;
        private List<videoDetail> video_list;
        private List<GuessData> guess_list;

        @Override
        public String toString() {
            return "detailData{" +
                    "library_id=" + library_id +
                    ", title='" + title + '\'' +
                    ", thumb_app_h='" + thumb_app_h + '\'' +
                    ", tags='" + tags + '\'' +
                    ", pv=" + pv +
                    ", main_speaker='" + main_speaker + '\'' +
                    ", description='" + description + '\'' +
                    ", url='" + url + '\'' +
                    ", is_free=" + is_free +
                    ", collect=" + collect +
                    ", historyid=" + historyid +
                    ", isOrder=" + isOrder +
                    ", library_detail_id=" + library_detail_id +
                    ", last_play_time=" + last_play_time +
                    ", video_list=" + video_list +
                    ", guess_list=" + guess_list +
                    '}';
        }

        public int getLibrary_id() {
            return library_id;
        }

        public void setLibrary_id(int library_id) {
            this.library_id = library_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getThumb_app_h() {
            return thumb_app_h;
        }

        public void setThumb_app_h(String thumb_app_h) {
            this.thumb_app_h = thumb_app_h;
        }

        public List<videoDetail> getVideo_list() {
            return video_list;
        }

        public void setVideo_list(List<videoDetail> video_list) {
            this.video_list = video_list;
        }

        public int getPv() {
            return pv;
        }

        public void setPv(int pv) {
            this.pv = pv;
        }

        public String getMain_speaker() {
            return main_speaker;
        }

        public void setMain_speaker(String main_speaker) {
            this.main_speaker = main_speaker;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getIs_free() {
            return is_free;
        }

        public void setIs_free(int is_free) {
            this.is_free = is_free;
        }

        public int getLast_play_time() {
            return last_play_time;
        }

        public void setLast_play_time(int last_play_time) {
            this.last_play_time = last_play_time;
        }


        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getLibrary_detail_id() {
            return library_detail_id;
        }

        public void setLibrary_detail_id(int library_detail_id) {
            this.library_detail_id = library_detail_id;
        }

        public int getCollect() {
            return collect;
        }

        public void setCollect(int collect) {
            this.collect = collect;
        }

        public int getHistoryid() {
            return historyid;
        }

        public void setHistoryid(int historyid) {
            this.historyid = historyid;
        }

        public int getIsOrder() {
            return isOrder;
        }

        public void setIsOrder(int isOrder) {
            this.isOrder = isOrder;
        }

        public class videoDetail implements Serializable {
            public int getLibrary_id() {
                return library_id;
            }

            private String mdstring;
            private int library_id;
            private int library_detail_id;
            private String video_name;
            private String code;
            private String video_duration;
            private String playurl;
            private String video;
            int last_play_time;
            int video_is_free;

            @Override
            public String toString() {
                return "videoDetail{" +
                        "mdstring='" + mdstring + '\'' +
                        ", library_id=" + library_id +
                        ", library_detail_id=" + library_detail_id +
                        ", video_name='" + video_name + '\'' +
                        ", code='" + code + '\'' +
                        ", video_duration='" + video_duration + '\'' +
                        ", playurl='" + playurl + '\'' +
                        ", video='" + video + '\'' +
                        ", last_play_time=" + last_play_time +
                        ", video_is_free=" + video_is_free +
                        '}';
            }

            public String getMdstring() {
                return mdstring;
            }

            public void setMdstring(String mdstring) {
                this.mdstring = mdstring;
            }

            public void setLibrary_id(int library_id) {
                this.library_id = library_id;
            }

            public int getLibrary_detail_id() {
                return library_detail_id;
            }

            public void setLibrary_detail_id(int library_detail_id) {
                this.library_detail_id = library_detail_id;
            }

            public String getVideo() {
                return video;
            }

            public void setVideo(String video) {
                this.video = video;
            }


            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }


            public String getPlayurl() {
                return playurl;
            }

            public void setPlayurl(String playurl) {
                this.playurl = playurl;
            }

            public String getVideo_name() {
                return video_name;
            }

            public void setVideo_name(String video_name) {
                this.video_name = video_name;
            }

            public String getVideo_duration() {
                return video_duration;
            }

            public void setVideo_duration(String video_duration) {
                this.video_duration = video_duration;
            }

            public int getLast_play_time() {
                return last_play_time;
            }

            public void setLast_play_time(int last_play_time) {
                this.last_play_time = last_play_time;
            }

            public int getVideo_is_free() {
                return video_is_free;
            }

            public void setVideo_is_free(int video_is_free) {
                this.video_is_free = video_is_free;
            }

        }

        public List<GuessData> getGuess_list() {
            return guess_list;
        }

        public void setGuess_list(List<GuessData> guess_list) {
            this.guess_list = guess_list;
        }

        public class GuessData implements Serializable {
            private String title;
            private String library_id;
            private String thumb_app_h;
            private int is_free;

            @Override
            public String toString() {
                return "GuessData{" +
                        "title='" + title + '\'' +
                        ", library_id='" + library_id + '\'' +
                        ", thumb_app_h='" + thumb_app_h + '\'' +
                        ", is_free=" + is_free +
                        '}';
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getLibrary_id() {
                return library_id;
            }

            public void setLibrary_id(String library_id) {
                this.library_id = library_id;
            }

            public String getThumb_app_h() {
                return thumb_app_h;
            }

            public void setThumb_app_h(String thumb_app_h) {
                this.thumb_app_h = thumb_app_h;
            }

            public int getIs_free() {
                return is_free;
            }

            public void setIs_free(int is_free) {
                this.is_free = is_free;
            }
        }
    }
}
