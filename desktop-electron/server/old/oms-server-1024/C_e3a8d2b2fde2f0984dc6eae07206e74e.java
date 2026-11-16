import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_e3a8d2b2fde2f0984dc6eae07206e74e
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('/kemp/ex_web/oms/app/datalist.html?dataname=OMS_FHYC_DPS?TYPE=CLDMID="+mapRow.get("DMID")+"');";return url;

    }
}
