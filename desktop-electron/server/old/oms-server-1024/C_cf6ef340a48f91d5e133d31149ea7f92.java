import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_cf6ef340a48f91d5e133d31149ea7f92
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('/kemp/ex_web/oms/app/datalist.html?dataname=OMS_FHYC_DPS&TYPE=CL&DMID="+mapRow.get("DMID")+"');";return url;

    }
}
