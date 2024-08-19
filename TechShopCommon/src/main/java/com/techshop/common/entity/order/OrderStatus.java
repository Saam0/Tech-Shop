package com.techshop.common.entity.order;

public enum OrderStatus {
    NEW{
        @Override
        public String defaultDescription() {
            return "Order was placed by the customer";
        }
    },
    CANCELLED{
        @Override
        public String defaultDescription() {
            return "Order was rejected";
        }
    },
    PROCESSING{
        @Override
        public String defaultDescription() {
            return "Order is being processed";
        }
    },
    PICKED{
        @Override
        public String defaultDescription() {
            return "Products were picked";
        }
    },
    PACKAGED{
        @Override
        public String defaultDescription() {
            return "Products were packaged";
        }
    },
    SHIPPING{
        @Override
        public String defaultDescription() {
            return "Shipper is delivering the package";
        }
    },
     DELIVERED{
        @Override
        public String defaultDescription() {
            return "Customer received the package";
        }
    },
    RETURNED{
        @Override
        public String defaultDescription() {
            return "Customer returned the package";
        }
    },
     PAID{
        @Override
        public String defaultDescription() {
            return "Customer paid for the order";
        }
     },
    REFUNDED{
        @Override
        public String defaultDescription() {
            return "Customer was refunded";
        }
    };
    public abstract String defaultDescription();
}
