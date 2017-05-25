import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OFPTables implements IOFPTable_Code{
    private static final Map<Integer,String> table_code = new HashMap<Integer, String>();
    static {
         table_code.put(1, "OUG Displacement vector");
         table_code.put(2, "OPG Load vector");
         table_code.put(3, "OQG SPCforce vector");
         table_code.put(4, "OEF Element force (or flux)");
         table_code.put(5, "OES Element stress(or strain)");
         table_code.put(6, "LAMA Eigenvalue summary"); 
         table_code.put(7, "OUG Eigenvector"); 
         table_code.put(8, "Obsolete"); 
         table_code.put(9, "OEIGS Eigenvalue analysis summary"); 
    }

    private static Map getMap() {
         return Collections.unmodifiableMap(table_code);
    }

	@Override
	public String getTable_CodeDescription(Integer type) {
		Map<Integer,String> table_codeMap = getMap();
		
		return table_codeMap.get(type);
	}
      
}
