import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_5f96feefe7ea6ff2fdd92b8d0aa09777
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String pdname = (mapRow.get("ID")==null || mapRow.get("ID").toString().trim().isEmpty())?"add":"edit";
String url="javascript:window.open('dataadd.html?dataname=OMS_JZB_EDIT&&actiontype="+pdname+"&pk_id="+mapRow.get("ID")+"');";
return url;

    }
}
