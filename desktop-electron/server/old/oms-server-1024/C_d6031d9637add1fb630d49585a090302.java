import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_d6031d9637add1fb630d49585a090302
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:showlayer('datalist.html?dataname=TRACE_ITEM_LIST&?trace_result_id="+mapRow.get("TRACE_RESULT_ID")+"','800px','500px');";return url;

    }
}
