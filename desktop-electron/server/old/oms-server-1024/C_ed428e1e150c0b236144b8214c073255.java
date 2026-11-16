import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_ed428e1e150c0b236144b8214c073255
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('module/bussines/zwdd/trace_linetotal.html?trace_result_id="+mapRow.get("TRACE_RESULT_ID")+"');";return url;

    }
}
