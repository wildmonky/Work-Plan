import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_2f877b42225b2ff73d19a155cc217b0d
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('datalist.html?dataname=CZXX_DDBHGL&find_sslx=0,2,3&find_czmc=&find_state_enable=1,2&find_mc="+mapRow.get("TDMC")+"');";return url;

    }
}
