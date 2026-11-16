import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_f842752862f1e6db9bf84107f1a9d0ef
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('datalist.html?dataname=TRACE_ITEM_LIST_LINE&trace_result_id="+mapRow.get("TRACE_RESULT_ID")+"');";return url;

    }
}
