import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_25c96b935e2c280dfeae5a9dc412fe24
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:showlayer('module/bussines/zwdd/trace_view.html?trace_result_id="+mapRow.get("TRACE_RESULT_ID")+"','800px','500px');";return url;

    }
}
