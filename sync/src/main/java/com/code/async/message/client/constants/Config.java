package com.code.async.message.client.constants;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public enum Config {
    ;

    public enum STATUS {
        OK("OK"),
        ERR("ERR"),
        TIMEOUT("TIMEOUT");
        private String type;

        STATUS(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    public enum ASYNC {
        async(1),
        sync(0),
        ;
        private Integer type;

        ASYNC(Integer type) {
            this.type = type;
        }

        public Integer getType() {
            return type;
        }
    }
}
