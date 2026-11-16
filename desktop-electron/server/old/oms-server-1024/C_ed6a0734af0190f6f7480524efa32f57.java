import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_ed6a0734af0190f6f7480524efa32f57
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('/kemp/ex_web/oms/app/datalist.html?dataname=OMS_FHYC_DPS&TYPE=CL&CLID="+mapRow.get("CLID")+"');";return url;

    }
}
