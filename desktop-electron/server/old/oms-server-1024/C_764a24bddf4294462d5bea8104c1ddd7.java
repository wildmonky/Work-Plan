import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_764a24bddf4294462d5bea8104c1ddd7
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('../omsserver/Zwdd/TraceDownload?trace_result_id="+mapRow.get("TRACE_RESULT_ID")+"');";return url;

    }
}
