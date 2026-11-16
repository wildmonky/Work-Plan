import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_ab953be04ea7b109674143a7183f7bca
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('module/bussines/zdh/print_pzbhd.html?id="+mapRow.get("DATA_ID")+"');";return url;

    }
}
