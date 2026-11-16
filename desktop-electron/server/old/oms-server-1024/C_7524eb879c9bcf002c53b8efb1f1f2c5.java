import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_7524eb879c9bcf002c53b8efb1f1f2c5
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String pdname = "+mapRow.get(" + pkName + ")+";String url="javascript:window.open('dataadd.html?dataname=OMS_JZB_EDIT&&actiontype=pdname&pk_id="+mapRow.get("ID")+"');";

    }
}
