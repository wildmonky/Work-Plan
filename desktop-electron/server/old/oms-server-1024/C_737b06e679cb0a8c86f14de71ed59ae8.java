import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_737b06e679cb0a8c86f14de71ed59ae8
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('datalist.html?dataname=ALARM_FREQUENT_DETAIL_RECORD&find_record_id="+mapRow.get("ID")+"');";return url;

    }
}
