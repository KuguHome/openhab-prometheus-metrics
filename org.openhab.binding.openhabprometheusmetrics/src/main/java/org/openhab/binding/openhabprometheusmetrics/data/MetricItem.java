package org.openhab.binding.openhabprometheusmetrics.data;

public class MetricItem {

    public MetricItem(String metricName, String metricValue) {
        super();
        this.metricName = metricName;
        this.metricValue = metricValue;
    }

    private String metricName;
    private String metricValue;

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(String metricValue) {
        this.metricValue = metricValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((metricName == null) ? 0 : metricName.hashCode());
        result = prime * result + ((metricValue == null) ? 0 : metricValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MetricItem other = (MetricItem) obj;
        if (metricName == null) {
            if (other.metricName != null) {
                return false;
            }
        } else if (!metricName.equals(other.metricName)) {
            return false;
        }
        if (metricValue == null) {
            if (other.metricValue != null) {
                return false;
            }
        } else if (!metricValue.equals(other.metricValue)) {
            return false;
        }
        return true;
    }

}
