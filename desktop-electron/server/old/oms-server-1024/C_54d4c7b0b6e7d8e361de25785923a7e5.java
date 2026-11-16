import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_54d4c7b0b6e7d8e361de25785923a7e5
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('dataview.html?dataname=ZDH_INSPECT&INSPECT_ID&actiontype=view&pk_id="+mapRow.get("INSPECT_ID")+"');";
return url;

    }
}
