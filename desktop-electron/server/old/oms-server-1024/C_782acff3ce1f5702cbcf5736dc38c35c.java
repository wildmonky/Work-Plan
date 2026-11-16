import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_782acff3ce1f5702cbcf5736dc38c35c
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('datalist.html?dataname=CZXX_DDBHGL&find_sslx=1&find_czmc=&find_state_enable=1,2&find_mc="+mapRow.get("TDMC")+"');";return url;

    }
}
