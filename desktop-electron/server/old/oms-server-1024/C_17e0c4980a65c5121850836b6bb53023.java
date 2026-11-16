import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_17e0c4980a65c5121850836b6bb53023
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('/kemp/ex_web/oms/app/module/bussines/zwdd/fhyc_value.html?ID="+mapRow.get("ID")+"');";return url;

    }
}
