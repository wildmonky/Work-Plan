import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_5ac3c8393fd4b94bccdeb44bcf5d3552
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="<a href='#' onclick=javascript:window.open('module/bussines/zwgj/linear_point.html?logId="+mapRow.get("ALARM_SWITCH_LOG_ID")+"')>"+mapRow.get("EQUIP_NAME")+"</a>";return url;

    }
}
