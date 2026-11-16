import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_9327d3e5ececb9f40e9be374f87afe5a
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="<a target=_blank href='#' onclick=javascript:showlayer('module/bussines/zwdd/trace_view.html?trace_result_id="+mapRow.get("TRACE_RESULT_ID")+"','800px','500px')>"+mapRow.get("EQU_NAME")+"</a>";return url;

    }
}
