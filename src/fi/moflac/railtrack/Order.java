package fi.moflac.railtrack;

public class Order {
	
	public String guid;
	public String title;
    public String from;
    public String to;
	public String pubDate;
	public String route;
	public String lateness;
	public String georss;
	public String status;
	public String eta;    //arvioitu tuloaika
	public String etd;
	public String scheda; //aikataulun mukainen saapumisaika
	public String schedd; //aikataulun mukainen lähtöaika
	
    public String getOrderName() {
        return title;
    }
    public void setOrderName(String orderName) {
        this.title = orderName;
    }
    public String getOrderStatus() {
        return from;
    }
    public void setOrderStatus(String orderStatus) {
        this.from = orderStatus;
    }
}
