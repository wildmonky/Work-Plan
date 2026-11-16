import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_e91f19dcbf0811afc69aa04ff85d669d
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String pdname = mapRow.get("ID").equals("")?"add":"edit";
String url="javascript:window.open('dataadd.html?dataname=OMS_JZB_EDIT&&actiontype="+pdname+"&pk_id="+mapRow.get("ID")+"');";

    }
}
