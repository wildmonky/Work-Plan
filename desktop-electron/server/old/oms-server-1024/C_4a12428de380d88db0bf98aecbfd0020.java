import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_4a12428de380d88db0bf98aecbfd0020
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        java.lang.Object _tmpVal = mapRow.get("ID");
String pdname = (_tmpVal != null && !_tmpVal.toString().trim().isEmpty()) ? "edit" : "add";
String url = "javascript:window.open('dataadd.html?dataname=" 
    + mapRow.get("OMS_JZB_EDIT") 
    + "&&actiontype=" 
    + pdname 
    + "&pk_id=" 
    + mapRow.get("ID") 
    + "');";
return url;

    }
}
