package barcode.dto;

public class ResultRowByItemsCollection<T,V> {
    private String quantDescr;
    private String sumDescr;
    private T quantValue;
    private V sumValue;

    public ResultRowByItemsCollection (String quantDescr,  T quantValue, String sumDescr, V sumValue) {
        this.quantDescr = quantDescr;
        this.sumDescr = sumDescr;
        this.quantValue = quantValue;
        this.sumValue = sumValue;
    }

    public String getQuantDescr() {
        return quantDescr;
    }

    public void setQuantDescr(String quantDescr) {
        this.quantDescr = quantDescr;
    }

    public String getSumDescr() {
        return sumDescr;
    }

    public void setSumDescr(String sumDescr) {
        this.sumDescr = sumDescr;
    }

    public T getQuantValue() {
        return quantValue;
    }

    public void setQuantValue(T quantValue) {
        this.quantValue = quantValue;
    }

    public V getSumValue() {
        return sumValue;
    }

    public void setSumValue(V sumValue) {
        this.sumValue = sumValue;
    }

}
