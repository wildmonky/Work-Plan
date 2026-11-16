import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_907c07fe48dd016d407d81f65dc442d6
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('flowform.html?flow_code=DDBHGL&data_id="+mapRow.get("ID")+"');";return url;

    }
}
