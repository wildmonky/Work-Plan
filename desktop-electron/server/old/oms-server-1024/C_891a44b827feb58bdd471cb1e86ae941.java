import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_891a44b827feb58bdd471cb1e86ae941
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="<a href='#' onclick=javascript:window.open('datalist.html?dataname=TRACE_ITEM_LIST&trace_result_id="+mapRow.get("TRACE_RESULT_ID")+"')>"+mapRow.get("EQU_NAME")+"</a>";return url;

    }
}
