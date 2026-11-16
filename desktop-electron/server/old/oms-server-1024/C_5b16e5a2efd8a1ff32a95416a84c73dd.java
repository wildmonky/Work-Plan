import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_5b16e5a2efd8a1ff32a95416a84c73dd
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('dmodule/bussines/zwdd/trace_view.html?trace_result_id="+mapRow.get("TRACE_RESULT_ID")+"');";return url;

    }
}
