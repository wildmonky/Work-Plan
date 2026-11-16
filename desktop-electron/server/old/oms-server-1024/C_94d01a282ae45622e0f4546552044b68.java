import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_94d01a282ae45622e0f4546552044b68
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('module/bussines/zwdd/trace_view.html?trace_result_id="+mapRow.get("TRACE_RESULT_ID")+"');";return url;

    }
}
