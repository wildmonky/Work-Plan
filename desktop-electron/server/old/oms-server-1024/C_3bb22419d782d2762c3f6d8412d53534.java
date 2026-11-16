import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_3bb22419d782d2762c3f6d8412d53534
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('../omsserver/Trace/Download?trace_result_id="+mapRow.get("TRACE_RESULT_ID")+"');";return url;

    }
}
