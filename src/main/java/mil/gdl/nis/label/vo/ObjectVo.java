package mil.gdl.nis.label.vo;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ObjectVo {
	
	private String id;
	
	private String x;
	
	private String y;
	
	private String ex;
	
	private String ey;
	
	private String objectInfoId;
	
	private String mmsi;
	
	private String imoNo;
	
	private String callSign;
	
	private String national;
	
	private String company;
	
	private String shipType;
	
	private String radar1;
	
	private String radar2;
	
	private String radar3;
	
	private String antiAircraft1;
	
	private String antiAircraft2;
	
	private String antiAircraft3;
	
    private String antiSubmarine1;
	
	private String antiSubmarine2;
	
	private String antiSubmarine3;
	
    private String antiShip1;
	
	private String antiShip2;
	
	private String antiShip3;
	
	private String antiFleet1;
		
	private String antiFleet2;
		
	private String antiFleet3;
	
    @Builder
    public ObjectVo(String id) {
        this.id = id;

    }
    

}
